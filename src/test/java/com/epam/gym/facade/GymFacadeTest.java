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
    void getTraineeProfile_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.findByUsername("Nazar.Volianskyi", "password123")).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeProfile("Nazar.Volianskyi", "password123");

        assertEquals(trainee, result);
    }

    @Test
    void changeTraineePassword_shouldDelegateToTraineeService() {
        gymFacade.changeTraineePassword("Nazar.Volianskyi", "old", "new");

        verify(traineeService, times(1)).changePassword("Nazar.Volianskyi", "old", "new");
    }

    @Test
    void updateTraineeProfile_shouldDelegateToTraineeService() {
        Trainee updateData = new Trainee();
        Trainee updated = new Trainee();
        when(traineeService.update("Nazar.Volianskyi", "password123", updateData)).thenReturn(updated);

        Trainee result = gymFacade.updateTraineeProfile("Nazar.Volianskyi", "password123", updateData);

        assertEquals(updated, result);
    }

    @Test
    void setTraineeActiveStatus_shouldDelegateToTraineeService() {
        gymFacade.setTraineeActiveStatus("Nazar.Volianskyi", "password123", false);

        verify(traineeService, times(1)).setActiveStatus("Nazar.Volianskyi", "password123", false);
    }

    @Test
    void deleteTraineeProfile_shouldDelegateToTraineeService() {
        gymFacade.deleteTraineeProfile("Nazar.Volianskyi", "password123");

        verify(traineeService, times(1)).delete("Nazar.Volianskyi", "password123");
    }

    @Test
    void getTraineeTrainings_shouldDelegateToTraineeService() {
        List<Training> trainings = List.of(new Training());
        when(traineeService.getTrainings("Nazar.Volianskyi", "password123", null, null, "Cardio")).thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings("Nazar.Volianskyi", "password123", null, null, "Cardio");

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldDelegateToTraineeService() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.getUnassignedTrainers("Nazar.Volianskyi", "password123")).thenReturn(trainers);

        List<Trainer> result = gymFacade.getUnassignedTrainers("Nazar.Volianskyi", "password123");

        assertEquals(1, result.size());
    }

    @Test
    void updateTraineeTrainersList_shouldDelegateToTraineeService() {
        Trainee updated = new Trainee();
        when(traineeService.updateTrainersList("Nazar.Volianskyi", "password123", List.of(1L))).thenReturn(updated);

        Trainee result = gymFacade.updateTraineeTrainersList("Nazar.Volianskyi", "password123", List.of(1L));

        assertEquals(updated, result);
    }

    @Test
    void createTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer(trainer);

        assertEquals(trainer, result);
    }

    @Test
    void getTrainerProfile_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("Nazar.Volianskyi", "password123")).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerProfile("Nazar.Volianskyi", "password123");

        assertEquals(trainer, result);
    }

    @Test
    void changeTrainerPassword_shouldDelegateToTrainerService() {
        gymFacade.changeTrainerPassword("Nazar.Volianskyi", "old123", "new123");

        verify(trainerService, times(1)).changePassword("Nazar.Volianskyi", "old123", "new123");
    }

    @Test
    void updateTrainerProfile_shouldDelegateToTrainerService() {
        Trainer updateData = new Trainer();
        Trainer updated = new Trainer();
        when(trainerService.update("Nazar.Volianskyi", "password123", updateData)).thenReturn(updated);

        Trainer result = gymFacade.updateTrainerProfile("Nazar.Volianskyi", "password123", updateData);

        assertEquals(updated, result);
    }

    @Test
    void setTrainerActiveStatus_shouldDelegateToTrainerService() {
        gymFacade.setTrainerActiveStatus("Nazar.Volianskyi", "password123", true);

        verify(trainerService, times(1)).setActiveStatus("Nazar.Volianskyi", "password123", true);
    }

    @Test
    void getTrainerTrainings_shouldDelegateToTrainerService() {
        List<Training> trainings = List.of(new Training());
        when(trainerService.getTrainings("Nazar.Volianskyi", "password123", null, null)).thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("Nazar.Volianskyi", "password123", null, null);

        assertEquals(1, result.size());
    }

    @Test
    void addTraining_shouldDelegateToTrainingService() {
        Training training = new Training();
        when(trainingService.addTraining("Nazar.Volianskyi", "password123", training)).thenReturn(training);

        Training result = gymFacade.addTraining("Nazar.Volianskyi", "password123", training);

        assertEquals(training, result);
    }
}