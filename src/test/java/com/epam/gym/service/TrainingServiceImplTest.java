package com.epam.gym.service;

import com.epam.gym.dao.TrainingDao;
import com.epam.gym.model.Training;
import com.epam.gym.util.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void addTraining_shouldAuthenticateAndPersist() {
        Training training = new Training();
        when(trainingDao.create(training)).thenReturn(training);
        Training result = trainingService.addTraining("Nazar.Volianskyi", "password123", training);

        assertEquals(training, result);
        verify(authenticationService, times(1)).authenticateTrainer("Nazar.Volianskyi", "password123");
    }
}