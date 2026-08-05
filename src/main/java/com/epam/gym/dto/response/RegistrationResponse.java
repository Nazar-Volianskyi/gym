package com.epam.gym.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationResponse {

    private String username;
    private String password;

    public RegistrationResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
