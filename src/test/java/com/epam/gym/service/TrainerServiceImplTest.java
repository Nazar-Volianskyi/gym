package com.epam.gym.service;

import com.epam.gym.dao.TrainerDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainer;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserProfileInitializer userProfileInitializer;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void create_shouldAssignIdAndInitializeProfile() {
        Trainer trainer = new Trainer();
        when(trainerDao.nextId()).thenReturn(1L);
        when(trainerDao.create(trainer)).thenReturn(trainer);
        Trainer result = trainerService.create(trainer);

        assertEquals(1L, result.getId());
        verify(userProfileInitializer, times(1)).initialize(trainer);
        verify(trainerDao, times(1)).create(trainer);
    }

    @Test
    void update_shouldUpdateTrainer_whenExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.update(trainer)).thenReturn(trainer);
        Trainer result = trainerService.update(trainer);

        assertEquals(trainer, result);
        verify(trainerDao, times(1)).update(trainer);
    }

    @Test
    void update_shouldThrowException_whenTrainerNotFound() {
        Trainer trainer = new Trainer();
        trainer.setId(999L);
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.update(trainer));
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        Trainer result = trainerService.findById(1L);

        assertEquals(trainer, result);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(trainerDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.findById(999L));
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerDao.findAll()).thenReturn(trainers);
        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size());
    }
}