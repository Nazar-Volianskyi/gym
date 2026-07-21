package com.epam.gym.service;

import com.epam.gym.model.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void delete(Long id);
    Trainee findById(Long id);
    List<Trainee> findAll();
}
