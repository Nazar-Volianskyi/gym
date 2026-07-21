package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
import com.epam.gym.util.UserProfileInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserProfileInitializer userProfileInitializer;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void create_shouldAssignIdAndInitializeProfile() {
        Trainee trainee = new Trainee();
        when(traineeDao.nextId()).thenReturn(1L);
        when(traineeDao.create(trainee)).thenReturn(trainee);
        Trainee result = traineeService.create(trainee);

        assertEquals(1L, result.getId());
        verify(userProfileInitializer, times(1)).initialize(trainee);
        verify(traineeDao, times(1)).create(trainee);
    }

    @Test
    void update_shouldUpdateTrainee_whenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);
        Trainee result = traineeService.update(trainee);

        assertEquals(trainee, result);
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void update_shouldThrowException_whenTraineeNotFound() {
        Trainee trainee = new Trainee();
        trainee.setId(999L);
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.update(trainee));
        verify(traineeDao, never()).update(any());
    }

    @Test
    void delete_shouldDeleteTrainee_whenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        traineeService.delete(1L);

        verify(traineeDao, times(1)).delete(1L);
    }

    @Test
    void delete_shouldThrowException_whenTraineeNotFound() {
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.delete(999L));
        verify(traineeDao, never()).delete(any());
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        Trainee result = traineeService.findById(1L);

        assertEquals(trainee, result);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(traineeDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.findById(999L));
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeDao.findAll()).thenReturn(trainees);
        List<Trainee> result = traineeService.findAll();

        assertEquals(2, result.size());
    }
}