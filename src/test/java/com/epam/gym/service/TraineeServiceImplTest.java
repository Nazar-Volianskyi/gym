package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.AuthenticationException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private Trainee buildTrainee(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setActive(true);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    void create_shouldInitializeProfileAndPersist() {
        Trainee trainee = buildTrainee(null, null);
        when(traineeDao.create(trainee)).thenReturn(trainee);

        Trainee result = traineeService.create(trainee);

        assertEquals(trainee, result);
        verify(userProfileInitializer, times(1)).initialize(trainee.getUser());
        verify(traineeDao, times(1)).create(trainee);
    }

    @Test
    void findByUsername_shouldReturnTrainee_whenAuthenticated() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);

        Trainee result = traineeService.findByUsername("John.Smith", "pass123");

        assertEquals(trainee, result);
    }

    @Test
    void findByUsername_shouldThrow_whenAuthenticationFails() {
        when(authenticationService.authenticateTrainee("John.Smith", "wrong"))
                .thenThrow(new AuthenticationException("Invalid username or password"));

        assertThrows(AuthenticationException.class,
                () -> traineeService.findByUsername("John.Smith", "wrong"));
    }

    @Test
    void changePassword_shouldUpdatePassword_whenAuthenticated() {
        Trainee trainee = buildTrainee("John.Smith", "oldPass");
        when(authenticationService.authenticateTrainee("John.Smith", "oldPass")).thenReturn(trainee);

        traineeService.changePassword("John.Smith", "oldPass", "newPass");

        assertEquals("newPass", trainee.getUser().getPassword());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void update_shouldChangeProfileFields() {
        Trainee existing = buildTrainee("John.Smith", "pass123");
        existing.setAddress("Kyiv");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(existing);
        when(traineeDao.update(existing)).thenReturn(existing);

        Trainee updatedData = buildTrainee(null, null);
        updatedData.setAddress("Lviv");

        Trainee result = traineeService.update("John.Smith", "pass123", updatedData);

        assertEquals("Lviv", result.getAddress());
        verify(traineeDao, times(1)).update(existing);
    }

    @Test
    void setActiveStatus_shouldChangeStatus_whenDifferentFromCurrent() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        trainee.getUser().setActive(true);
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);

        traineeService.setActiveStatus("John.Smith", "pass123", false);

        assertEquals(false, trainee.getUser().isActive());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void setActiveStatus_shouldSkipUpdate_whenStatusAlreadySame() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        trainee.getUser().setActive(true);
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);

        traineeService.setActiveStatus("John.Smith", "pass123", true);

        verify(traineeDao, times(0)).update(trainee);
    }

    @Test
    void delete_shouldDeleteTrainee_whenAuthenticated() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);

        traineeService.delete("John.Smith", "pass123");

        verify(traineeDao, times(1)).delete(trainee);
    }

    @Test
    void getTrainings_shouldReturnFilteredTrainings() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);
        List<Training> trainings = List.of(new Training());
        when(trainingDao.findTraineeTrainings("John.Smith", null, null, "Cardio")).thenReturn(trainings);

        List<Training> result = traineeService.getTrainings("John.Smith", "pass123", null, null, "Cardio");

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldReturnList() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerDao.findTrainersNotAssignedToTrainee("John.Smith")).thenReturn(trainers);

        List<Trainer> result = traineeService.getUnassignedTrainers("John.Smith", "pass123");

        assertEquals(1, result.size());
    }

    @Test
    void updateTrainersList_shouldSetTrainersOnTrainee() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerDao.findById(1L)).thenReturn(java.util.Optional.of(trainer));
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainersList("John.Smith", "pass123", List.of(1L));

        assertEquals(1, result.getTrainers().size());
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void updateTrainersList_shouldThrow_whenTrainerNotFound() {
        Trainee trainee = buildTrainee("John.Smith", "pass123");
        when(authenticationService.authenticateTrainee("John.Smith", "pass123")).thenReturn(trainee);
        when(trainerDao.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> traineeService.updateTrainersList("John.Smith", "pass123", List.of(999L)));
    }
}