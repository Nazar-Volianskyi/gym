package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TraineeInfoResponse;
import com.epam.gym.dto.response.TrainerProfileResponse;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainerMapper {

    public TrainerProfileResponse toResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setUsername(trainer.getUser().getUsername());
        response.setFirstName(trainer.getUser().getFirstName());
        response.setLastName(trainer.getUser().getLastName());
        response.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        response.setActive(trainer.getUser().isActive());
        response.setTrainees(mapTrainees(trainer.getTrainees()));
        return response;
    }

    private List<TraineeInfoResponse> mapTrainees(List<Trainee> trainees) {
        return trainees.stream().map(this::toTraineeInfo).toList();
    }

    private TraineeInfoResponse toTraineeInfo(Trainee trainee) {
        TraineeInfoResponse info = new TraineeInfoResponse();
        info.setUsername(trainee.getUser().getUsername());
        info.setFirstName(trainee.getUser().getFirstName());
        info.setLastName(trainee.getUser().getLastName());
        return info;
    }
}
