package com.epam.gym.service;

import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {
    Trainer create(Trainer trainer);
    Trainer findByUsername(String username, String password);
    void changePassword(String username, String password, String newPassword);
    Trainer update(String username, String password, Trainer updatedData);
    void setActiveStatus(String username, String password, boolean isActive);
    List<Training> getTrainings(String username, String password, LocalDate fromDate, LocalDate toDate);
}
