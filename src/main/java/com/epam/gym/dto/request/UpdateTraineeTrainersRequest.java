package com.epam.gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateTraineeTrainersRequest {

    @NotBlank
    private String traineeUsername;

    @NotEmpty
    private List<String> trainerUsernames;
}
