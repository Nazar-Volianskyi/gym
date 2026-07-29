package com.epam.gym.util;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.exception.AuthenticationException;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Trainee buildTrainee(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    private Trainer buildTrainer(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    @Test
    void authenticateTrainee_shouldReturnTrainee_whenCredentialsMatch() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi", "secret123");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        Trainee result = authenticationService.authenticateTrainee("Nazar.Volianskyi", "secret123");

        assertEquals(trainee, result);
    }

    @Test
    void authenticateTrainee_shouldThrow_whenUsernameNotFound() {
        when(traineeDao.findByUsername("Noname")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticateTrainee("Noname", "any"));
    }

    @Test
    void authenticateTrainee_shouldThrow_whenPasswordDoesNotMatch() {
        Trainee trainee = buildTrainee("Nazar.Volianskyi", "secret123");
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticateTrainee("Nazar.Volianskyi", "wrongPassword"));
    }

    @Test
    void authenticateTrainer_shouldReturnTrainer_whenCredentialsMatch() {
        Trainer trainer = buildTrainer("Anna.Kovalenko", "pass456");
        when(trainerDao.findByUsername("Anna.Kovalenko")).thenReturn(Optional.of(trainer));
        Trainer result = authenticationService.authenticateTrainer("Anna.Kovalenko", "pass456");

        assertEquals(trainer, result);
    }

    @Test
    void authenticateTrainer_shouldThrow_whenUsernameNotFound() {
        when(trainerDao.findByUsername("Noname")).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticateTrainer("Noname", "any"));
    }

    @Test
    void authenticateTrainer_shouldThrow_whenPasswordDoesNotMatch() {
        Trainer trainer = buildTrainer("Anna.Kovalenko", "pass456");
        when(trainerDao.findByUsername("Anna.Kovalenko")).thenReturn(Optional.of(trainer));
        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticateTrainer("Anna.Kovalenko", "wrongPassword"));
    }
}