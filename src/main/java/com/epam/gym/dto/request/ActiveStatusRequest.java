package com.epam.gym.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActiveStatusRequest {

    @NotNull
    private Boolean active;
}
