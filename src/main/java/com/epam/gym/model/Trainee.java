package com.epam.gym.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(Long id, String firstName, String lastName, String username,
                   String password, boolean isActive, LocalDate dateOfBirth, String address) {
        super(id, firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

}
