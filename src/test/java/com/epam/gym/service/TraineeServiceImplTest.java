package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserProfileInitializer userProfileInitializer;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee buildTrainee(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        user.setActive(true);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    void create_shouldInitializeProfileAndPersist_whenNoConflict() {
        Trainee trainee = buildTrainee(null);
        when(trainerDao.existsByFullName("Nazar", "Volianskyi")).thenReturn(false);
        when(traineeDao.create(trainee)).thenReturn(trainee);
        Trainee result = traineeService.create(trainee);

        assertEquals(trainee, result);
        verify(userProfileInitializer, times(1)).initialize(trainee.getUser());
        verify(traineeDao, times(1)).create(trainee);
    }

    @Test
    void create_shouldThrowConflict_whenSameNameAlreadyRegisteredAsTrainer() {
        Trainee trainee = buildTrainee(null);
        when(trainerDao.existsByFullName("Nazar", "Volianskyi")).thenReturn(true);
        assertThrows(ConflictException.class, () -> traineeService.create(trainee));
        verify(traineeDao, never()).create(trainee);
        verify(userProfileInitializer, never()).initialize(trainee.getUser());
    }

    @Test
    void findByUsername_shouldReturnTrainee_whenFound() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("Nazar.Volianskyi");

        assertEquals(trainee, result);
    }

    @Test
    void findByUsername_shouldThrow_whenNotFound() {
        when(traineeDao.findByUsername("Nobody")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> traineeService.findByUsername("Nobody"));
    }

    @Test
    void changePassword_shouldUpdatePassword_whenAuthenticated() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        trainee.getUser().setPassword("oldPass");
        when(authenticationService.authenticateTrainee("Nazar.Volianskyi", "oldPass")).thenReturn(trainee);

        traineeService.changePassword("Nazar.Volianskyi", "oldPass", "newPass");

        assertEquals("newPass", trainee.getUser().getPassword());
    }

    @Test
    void update_shouldChangeProfileFields() {
        Trainee existing = buildTrainee("Nazar.Volianskyi");
        existing.setAddress("Kyiv");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(existing));
        when(traineeDao.update(existing)).thenReturn(existing);

        Trainee updatedData = buildTrainee(null);
        updatedData.setAddress("Lviv");

        Trainee result = traineeService.update("Nazar.Volianskyi", updatedData, false);

        assertEquals("Lviv", result.getAddress());
        assertFalse(result.getUser().isActive());
    }

    @Test
    void setActiveStatus_shouldChangeStatus_whenDifferentFromCurrent() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        trainee.getUser().setActive(true);
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        traineeService.setActiveStatus("Nazar.Volianskyi", false);

        assertFalse(trainee.getUser().isActive());
    }

    @Test
    void setActiveStatus_shouldThrowConflict_whenStatusAlreadySame() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        trainee.getUser().setActive(true);
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        assertThrows(ConflictException.class, () -> traineeService.setActiveStatus("Nazar.Volianskyi", true));
    }

    @Test
    void delete_shouldDeleteTrainee_whenFound() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        traineeService.delete("Nazar.Volianskyi");

        verify(traineeDao, times(1)).delete(trainee);
    }

    @Test
    void getTrainings_shouldReturnFilteredTrainings() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));
        List<Training> trainings = List.of(new Training());
        when(trainingDao.findTraineeTrainings("Nazar.Volianskyi", null, null, "Nazar.Volianskyi", "Cardio"))
                .thenReturn(trainings);

        List<Training> result = traineeService.getTrainings("Nazar.Volianskyi", null, null, "Nazar.Volianskyi", "Cardio");

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldReturnList() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerDao.findTrainersNotAssignedToTrainee("Nazar.Volianskyi")).thenReturn(trainers);

        List<Trainer> result = traineeService.getUnassignedTrainers("Nazar.Volianskyi");

        assertEquals(1, result.size());
    }

    @Test
    void updateTrainersList_shouldSetTrainersOnTrainee() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        Trainer trainer = new Trainer();
        when(trainerDao.findByUsername("Mark.Dok")).thenReturn(Optional.of(trainer));
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainersList("Nazar.Volianskyi", List.of("Mark.Dok"));

        assertEquals(1, result.getTrainers().size());
    }

}
