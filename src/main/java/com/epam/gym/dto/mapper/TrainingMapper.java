package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TraineeTrainingResponse;
import com.epam.gym.dto.response.TrainerTrainingResponse;
import com.epam.gym.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TraineeTrainingResponse toTraineeTrainingResponse(Training training) {
        TraineeTrainingResponse response = new TraineeTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setTrainingDuration(training.getTrainingDuration());
        response.setTrainerName(fullName(training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName()));
        return response;
    }

    public TrainerTrainingResponse toTrainerTrainingResponse(Training training) {
        TrainerTrainingResponse response = new TrainerTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setTrainingDuration(training.getTrainingDuration());
        response.setTraineeName(fullName(training.getTrainee().getUser().getFirstName(),
                training.getTrainee().getUser().getLastName()));
        return response;
    }

    private String fullName(String firstName, String lastName) {
        return "%s %s".formatted(firstName, lastName);
    }
}
