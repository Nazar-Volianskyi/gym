package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.dao.TrainingTypeDao;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import com.epam.gym.util.AuthenticationService;
import com.epam.gym.util.UserProfileInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private UserProfileInitializer userProfileInitializer;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer buildTrainer(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Cardio");
        trainer.setSpecialization(type);
        return trainer;
    }

    @Test
    void create_shouldInitializeProfileAndPersist_whenNoConflict() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");
        when(traineeDao.existsByFullName("Nazar", "Volianskyi")).thenReturn(false);
        when(trainingTypeDao.findByName("Cardio")).thenReturn(Optional.of(cardio));
        when(trainerDao.create(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create("Nazar", "Volianskyi", "Cardio");

        assertEquals("Nazar", result.getUser().getFirstName());
        assertEquals("Cardio", result.getSpecialization().getTrainingTypeName());
    }

    @Test
    void create_shouldThrowConflict_whenSameNameAlreadyRegisteredAsTrainee() {
        when(traineeDao.existsByFullName("Nazar", "Volianskyi")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> trainerService.create("Nazar", "Volianskyi", "Cardio"));
        verify(trainerDao, never()).create(any());
    }

    @Test
    void create_shouldThrow_whenSpecializationNotFound() {
        when(traineeDao.existsByFullName("Nazar", "Volianskyi")).thenReturn(false);
        when(trainingTypeDao.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainerService.create("Nazar", "Volianskyi", "Unknown"));
    }

    @Test
    void findByUsername_shouldReturnTrainer_whenFound() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        when(trainerDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("Nazar.Volianskyi");

        assertEquals(trainer, result);
    }

    @Test
    void findByUsername_shouldThrow_whenNotFound() {
        when(trainerDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.findByUsername("Nobody"));
    }

    @Test
    void changePassword_shouldUpdatePassword_whenAuthenticated() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        trainer.getUser().setPassword("oldPass");
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "oldPass")).thenReturn(trainer);

        trainerService.changePassword("Nazar.Volianskyi", "oldPass", "newPass");

        assertEquals("newPass", trainer.getUser().getPassword());
    }

    @Test
    void update_shouldChangeNameAndActiveStatus_butNotSpecialization() {
        Trainer existing = buildTrainer("Nazar.Volianskyi");
        when(trainerDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(existing));
        when(trainerDao.update(existing)).thenReturn(existing);

        User updatedUser = new User();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        Trainer updatedData = new Trainer();
        updatedData.setUser(updatedUser);
        TrainingType yoga = new TrainingType();
        yoga.setTrainingTypeName("Yoga");
        updatedData.setSpecialization(yoga);

        Trainer result = trainerService.update("Nazar.Volianskyi", updatedData, false);

        assertEquals("Updated", result.getUser().getFirstName());
        assertFalse(result.getUser().isActive());
        assertEquals("Cardio", result.getSpecialization().getTrainingTypeName());
    }

    @Test
    void setActiveStatus_shouldChangeStatus_whenDifferentFromCurrent() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        trainer.getUser().setActive(true);
        when(trainerDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainer));

        trainerService.setActiveStatus("Nazar.Volianskyi", false);

        assertFalse(trainer.getUser().isActive());
    }

    @Test
    void setActiveStatus_shouldThrowConflict_whenStatusAlreadySame() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        trainer.getUser().setActive(true);
        when(trainerDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainer));

        assertThrows(ConflictException.class, () -> trainerService.setActiveStatus("Nazar.Volianskyi", true));
        verify(trainerDao, never()).update(trainer);
    }

    @Test
    void getTrainings_shouldReturnFilteredTrainings() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        when(trainerDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainer));
        List<Training> trainings = List.of(new Training());
        when(trainingDao.findTrainerTrainings("Nazar.Volianskyi", null, null,
                "Nazar.Volianskyi"))
                .thenReturn(trainings);

        List<Training> result = trainerService.getTrainings("Nazar.Volianskyi", null, null,
                "Nazar.Volianskyi");

        assertEquals(1, result.size());
    }
}
