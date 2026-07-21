package com.epam.gym.dao;

import com.epam.gym.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee create(Trainee trainee);
    Trainee update(Trainee trainee);
    void delete(Long id);
    Optional<Trainee> findById(Long id);
    List<Trainee> findAll();
    Long nextId();


}
