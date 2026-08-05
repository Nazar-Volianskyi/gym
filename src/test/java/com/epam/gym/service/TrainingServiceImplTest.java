package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void addTraining_shouldResolveTraineeAndTrainer_andDeriveTrainingTypeFromTrainerSpecialization() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");
        trainer.setSpecialization(cardio);

        when(traineeDao.findByUsername("Nazar1.Volianskyi")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("Nazar2.Volianskyi")).thenReturn(Optional.of(trainer));
        when(trainingDao.create(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate date = LocalDate.of(2026, 8, 4);
        Training result = trainingService.addTraining("Nazar1.Volianskyi", "Nazar2.Volianskyi",
                "Morning Cardio", date, 60);

        assertEquals(trainee, result.getTrainee());
        assertEquals(trainer, result.getTrainer());
        assertEquals("Morning Cardio", result.getTrainingName());
        assertEquals(cardio, result.getTrainingType());
        assertEquals(date, result.getTrainingDate());
        assertEquals(60, result.getTrainingDuration());
        verify(trainingDao, times(1)).create(result);
    }

    @Test
    void addTraining_shouldThrow_whenTraineeNotFound() {
        when(traineeDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining("Nobody", "Nazar.Volianskyi",
                        "Cardio", LocalDate.now(), 60));
    }

    @Test
    void addTraining_shouldThrow_whenTrainerNotFound() {
        when(traineeDao.findByUsername("Nazar.Volianskyi")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining("Nazar.Volianskyi", "Nobody", "Cardio",
                        LocalDate.now(), 60));
    }
}
