package com.epam.gym.facade;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.service.TraineeService;
import com.epam.gym.service.TrainerService;
import com.epam.gym.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    void createTrainee_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.create(trainee)).thenReturn(trainee);
        Trainee result = gymFacade.createTrainee(trainee);

        assertEquals(trainee, result);
        verify(traineeService, times(1)).create(trainee);
    }

    @Test
    void updateTrainee_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeService.update(trainee)).thenReturn(trainee);
        Trainee result = gymFacade.updateTrainee(trainee);

        assertEquals(trainee, result);
        verify(traineeService, times(1)).update(trainee);
    }

    @Test
    void deleteTrainee_shouldDelegateToTraineeService() {
        gymFacade.deleteTrainee(1L);

        verify(traineeService, times(1)).delete(1L);
    }

    @Test
    void getTrainee_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeService.findById(1L)).thenReturn(trainee);
        Trainee result = gymFacade.getTrainee(1L);

        assertEquals(trainee, result);
    }

    @Test
    void getAllTrainees_shouldDelegateToTraineeService() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeService.findAll()).thenReturn(trainees);
        List<Trainee> result = gymFacade.getAllTrainees();

        assertEquals(2, result.size());
    }

    @Test
    void createTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.create(trainer)).thenReturn(trainer);
        Trainer result = gymFacade.createTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService, times(1)).create(trainer);
    }

    @Test
    void updateTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerService.update(trainer)).thenReturn(trainer);
        Trainer result = gymFacade.updateTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService, times(1)).update(trainer);
    }

    @Test
    void getTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerService.findById(1L)).thenReturn(trainer);
        Trainer result = gymFacade.getTrainer(1L);

        assertEquals(trainer, result);
    }

    @Test
    void getAllTrainers_shouldDelegateToTrainerService() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());
        when(trainerService.findAll()).thenReturn(trainers);
        List<Trainer> result = gymFacade.getAllTrainers();

        assertEquals(2, result.size());
    }

    @Test
    void createTraining_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.create(training)).thenReturn(training);
        Training result = gymFacade.createTraining(training);

        assertEquals(training, result);
        verify(trainingService, times(1)).create(training);
    }

    @Test
    void getTraining_shouldDelegateToTrainingService() {
        Training training = new Training();
        training.setId(1L);
        when(trainingService.findById(1L)).thenReturn(training);
        Training result = gymFacade.getTraining(1L);

        assertEquals(training, result);
    }

    @Test
    void getAllTrainings_shouldDelegateToTrainingService() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingService.findAll()).thenReturn(trainings);
        List<Training> result = gymFacade.getAllTrainings();

        assertEquals(2, result.size());
    }
}