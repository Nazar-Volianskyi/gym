package com.epam.gym.service;

import com.epam.gym.model.Training;

import java.time.LocalDate;

public interface TrainingService {
    Training addTraining(String traineeUsername, String trainerUsername, String trainingName,
                         LocalDate trainingDate, int trainingDuration);
}
