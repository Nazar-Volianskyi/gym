package com.epam.gym.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainerInfoResponse {

    private String username;
    private String firstName;
    private String lastName;
    private String specialization;

}