package com.epam.gym.service;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    Trainee create(Trainee trainee);
    Trainee findByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainee update(String username, Trainee updatedData, boolean isActive);
    void setActiveStatus(String username, boolean isActive);
    void delete(String username);
    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                String trainerName, String trainingTypeName);
    List<Trainer> getUnassignedTrainers(String username);
    Trainee updateTrainersList(String username, List<String> trainerUsernames);
}
