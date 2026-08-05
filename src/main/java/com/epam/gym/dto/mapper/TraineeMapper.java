package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TraineeProfileResponse;
import com.epam.gym.dto.response.TrainerInfoResponse;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeMapper {

    public TraineeProfileResponse toResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setUsername(trainee.getUser().getUsername());
        response.setFirstName(trainee.getUser().getFirstName());
        response.setLastName(trainee.getUser().getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.getUser().isActive());
        response.setTrainers(mapTrainers(trainee.getTrainers()));
        return response;
    }

    public List<TrainerInfoResponse> mapTrainers(List<Trainer> trainers) {
        return trainers.stream().map(this::toTrainerInfo).toList();
    }

    private TrainerInfoResponse toTrainerInfo(Trainer trainer) {
        TrainerInfoResponse info = new TrainerInfoResponse();
        info.setUsername(trainer.getUser().getUsername());
        info.setFirstName(trainer.getUser().getFirstName());
        info.setLastName(trainer.getUser().getLastName());
        info.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        return info;
    }
}