package com.epam.gym.service;

import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.AuthenticationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private UserProfileInitializer userProfileInitializer;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer buildTrainer(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
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
    void create_shouldInitializeProfileAndPersist() {
        Trainer trainer = buildTrainer(null, null);
        when(trainerDao.create(trainer)).thenReturn(trainer);

        Trainer result = trainerService.create(trainer);

        assertEquals(trainer, result);
        verify(userProfileInitializer, times(1)).initialize(trainer.getUser());
        verify(trainerDao, times(1)).create(trainer);
    }

    @Test
    void findByUsername_shouldReturnTrainer_whenAuthenticated() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi", "password123");
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "password123")).thenReturn(trainer);

        Trainer result = trainerService.findByUsername("Nazar.Volianskyi", "password123");

        assertEquals(trainer, result);
    }

    @Test
    void findByUsername_shouldThrow_whenAuthenticationFails() {
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password"));

        assertThrows(AuthenticationException.class,
                () -> trainerService.findByUsername("Nazar.Volianskyi", "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword_whenAuthenticated() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi", "oldPass");
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "oldPass")).thenReturn(trainer);

        trainerService.changePassword("Nazar.Volianskyi", "oldPass", "newPass");

        assertEquals("newPass", trainer.getUser().getPassword());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void update_shouldChangeSpecialization() {
        Trainer existing = buildTrainer("Nazar.Volianskyi", "pass123");
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "pass123")).thenReturn(existing);
        when(trainerDao.update(existing)).thenReturn(existing);

        TrainingType yoga = new TrainingType();
        yoga.setTrainingTypeName("Yoga");
        Trainer updatedData = buildTrainer(null, null);
        updatedData.setSpecialization(yoga);
        Trainer result = trainerService.update("Nazar.Volianskyi", "pass123", updatedData);

        assertEquals("Yoga", result.getSpecialization().getTrainingTypeName());
        verify(trainerDao, times(1)).update(existing);
    }

    @Test
    void setActiveStatus_shouldChangeStatus_whenDifferentFromCurrent() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi", "pass123");
        trainer.getUser().setActive(true);
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "pass123")).thenReturn(trainer);

        trainerService.setActiveStatus("Nazar.Volianskyi", "pass123", false);

        assertFalse(trainer.getUser().isActive());
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void setActiveStatus_shouldSkipUpdate_whenStatusAlreadySame() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi", "pass123");
        trainer.getUser().setActive(true);
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "pass123")).thenReturn(trainer);

        trainerService.setActiveStatus("Nazar.Volianskyi", "pass123", true);

        verify(trainerDao, times(0)).update(trainer);
    }

    @Test
    void getTrainings_shouldReturnFilteredTrainings() {
        Trainer trainer = buildTrainer("Nazar.Volianskyi", "pass123");
        when(authenticationService.authenticateTrainer("Nazar.Volianskyi", "pass123")).thenReturn(trainer);
        List<Training> trainings = List.of(new Training());
        when(trainingDao.findTrainerTrainings("Nazar.Volianskyi", null, null)).thenReturn(trainings);

        List<Training> result = trainerService.getTrainings("Nazar.Volianskyi", "pass123", null, null);

        assertEquals(1, result.size());
    }
}