package com.epam.gym.service;

import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void create_shouldAssignIdAndDelegateToDao() {
        Training training = new Training();
        when(trainingDao.nextId()).thenReturn(1L);
        when(trainingDao.create(training)).thenReturn(training);
        Training result = trainingService.create(training);

        assertEquals(1L, result.getId());
        verify(trainingDao, times(1)).create(training);
    }

    @Test
    void findById_shouldReturnTraining_whenExists() {
        Training training = new Training();
        training.setId(1L);
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));
        Training result = trainingService.findById(1L);

        assertEquals(training, result);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(trainingDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.findById(999L));
    }

    @Test
    void findAll_shouldReturnAllTrainings() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingDao.findAll()).thenReturn(trainings);
        List<Training> result = trainingService.findAll();

        assertEquals(2, result.size());
    }
}