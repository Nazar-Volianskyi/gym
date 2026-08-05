package com.epam.gym.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TrainerProfileResponse {

    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean active;
    private List<TraineeInfoResponse> trainees;
}
