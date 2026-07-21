package com.epam.gym.service;

import com.epam.gym.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer create(Trainer trainer);
    Trainer update(Trainer trainer);
    Trainer findById(Long id);
    List<Trainer> findAll();
}
