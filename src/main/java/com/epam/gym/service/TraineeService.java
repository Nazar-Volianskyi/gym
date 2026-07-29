package com.epam.gym.service;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    Trainee create(Trainee trainee);
    Trainee findByUsername(String username, String password);
    void changePassword(String username, String password, String newPassword);
    Trainee update(String username, String password, Trainee updatedData);
    void setActiveStatus(String username, String password, boolean isActive);
    void delete(String username, String password);
    List<Training> getTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                String trainingTypeName);
    List<Trainer> getUnassignedTrainers(String username, String password);
    Trainee updateTrainersList(String username, String password, List<Long> trainerIds);
}
