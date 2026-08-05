package com.epam.gym.service;

import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {
    Trainer create(String firstName, String lastName, String specialization);
    Trainer findByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainer update(String username, Trainer updatedData, boolean isActive);
    void setActiveStatus(String username, boolean isActive);
    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
}
