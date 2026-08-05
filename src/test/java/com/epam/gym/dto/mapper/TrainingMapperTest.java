package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TraineeTrainingResponse;
import com.epam.gym.dto.response.TrainerTrainingResponse;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    private final TrainingMapper mapper = new TrainingMapper();

    private Training buildTraining() {
        User traineeUser = new User();
        traineeUser.setFirstName("Nazar");
        traineeUser.setLastName("Volianskyi");
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setFirstName("Nazar1");
        trainerUser.setLastName("Volianskyi");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(cardio);
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 1, 15));
        training.setTrainingDuration(60);
        return training;
    }

    @Test
    void toTraineeTrainingResponse_shouldMapAllFieldsWithTrainerFullName() {
        TraineeTrainingResponse response = mapper.toTraineeTrainingResponse(buildTraining());

        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals(LocalDate.of(2026, 1, 15), response.getTrainingDate());
        assertEquals("Cardio", response.getTrainingType());
        assertEquals(60, response.getTrainingDuration());
        assertEquals("Nazar1 Volianskyi", response.getTrainerName());
    }

    @Test
    void toTrainerTrainingResponse_shouldMapAllFieldsWithTraineeFullName() {
        TrainerTrainingResponse response = mapper.toTrainerTrainingResponse(buildTraining());

        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals(LocalDate.of(2026, 1, 15), response.getTrainingDate());
        assertEquals("Cardio", response.getTrainingType());
        assertEquals(60, response.getTrainingDuration());
        assertEquals("Nazar Volianskyi", response.getTraineeName());
    }
}
