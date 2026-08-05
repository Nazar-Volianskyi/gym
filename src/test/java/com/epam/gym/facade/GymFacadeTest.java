package com.epam.gym.facade;

import com.epam.gym.exception.EntityNotFoundException;
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

import java.time.LocalDate;
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
    }

    @Test
    void getTraineeProfile_shouldDelegateToTraineeService() {
        Trainee trainee = new Trainee();
        when(traineeService.findByUsername("Nazar.Volianskyi")).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeProfile("Nazar.Volianskyi");

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
        when(traineeService.update("Nazar.Volianskyi", updateData, true)).thenReturn(updated);

        Trainee result = gymFacade.updateTraineeProfile("Nazar.Volianskyi", updateData, true);

        assertEquals(updated, result);
    }


    @Test
    void getTraineeTrainings_shouldDelegateToTraineeService() {
        List<Training> trainings = List.of(new Training());
        when(traineeService.getTrainings("Nazar.Volianskyi", null, null, "Nazar.Volianskyi",
                "Cardio"))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings(
                "Nazar.Volianskyi", null, null, "Nazar.Volianskyi", "Cardio");

        assertEquals(1, result.size());
    }

    @Test
    void getUnassignedTrainers_shouldDelegateToTraineeService() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeService.getUnassignedTrainers("Nazar.Volianskyi")).thenReturn(trainers);

        List<Trainer> result = gymFacade.getUnassignedTrainers("Nazar.Volianskyi");

        assertEquals(1, result.size());
    }

    @Test
    void updateTraineeTrainersList_shouldDelegateToTraineeService() {
        Trainee updated = new Trainee();
        when(traineeService.updateTrainersList("Nazar.Volianskyi", List.of("Nazar1.Volianskyi"))).thenReturn(updated);

        Trainee result = gymFacade.updateTraineeTrainersList("Nazar.Volianskyi", List.of("Nazar1.Volianskyi"));

        assertEquals(updated, result);
    }

    @Test
    void createTrainer_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.create("Nazar", "Volianskyi", "Cardio")).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer("Nazar", "Volianskyi", "Cardio");

        assertEquals(trainer, result);
    }

    @Test
    void getTrainerProfile_shouldDelegateToTrainerService() {
        Trainer trainer = new Trainer();
        when(trainerService.findByUsername("Nazar.Volianskyi")).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerProfile("Nazar.Volianskyi");

        assertEquals(trainer, result);
    }


    @Test
    void updateTrainerProfile_shouldDelegateToTrainerService() {
        Trainer updateData = new Trainer();
        Trainer updated = new Trainer();
        when(trainerService.update("Nazar.Volianskyi", updateData, true)).thenReturn(updated);

        Trainer result = gymFacade.updateTrainerProfile("Nazar.Volianskyi", updateData, true);

        assertEquals(updated, result);
    }


    @Test
    void getTrainerTrainings_shouldDelegateToTrainerService() {
        List<Training> trainings = List.of(new Training());
        when(trainerService.getTrainings("Nazar1.Volianskyi", null, null, "Nazar.Volianskyi")).thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("Nazar1.Volianskyi", null, null, "Nazar.Volianskyi");

        assertEquals(1, result.size());
    }

    @Test
    void addTraining_shouldDelegateToTrainingService() {
        Training training = new Training();
        LocalDate date = LocalDate.of(2026, 8, 4);
        when(trainingService.addTraining("Nazar.Volianskyi", "Nazar.Volianskyi", "Morning Cardio", date, 60))
                .thenReturn(training);

        Training result = gymFacade.addTraining("Nazar.Volianskyi", "Nazar.Volianskyi", "Morning Cardio", date, 60);

        assertEquals(training, result);
    }

    @Test
    void changeLogin_shouldChangeTrainerPassword_whenUsernameDoesNotBelongToTrainee() {
        when(traineeService.findByUsername("Nazar.Volianskyi"))
                .thenThrow(new EntityNotFoundException("Trainee", "Nazar.Volianskyi"));

        gymFacade.changeLogin("Nazar.Volianskyi", "old", "new");

        verify(trainerService, times(1)).changePassword("Nazar.Volianskyi", "old", "new");
    }
}
