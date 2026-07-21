package com.epam.gym.service;

import com.epam.gym.model.Training;

import java.util.List;

public interface TrainingService {
    Training create(Training training);
    Training findById(Long id);
    List<Training> findAll();
}
