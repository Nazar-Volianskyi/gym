package com.epam.gym.util;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.exception.AuthenticationException;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Trainee authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        if (!trainee.getUser().getPassword().equals(password)) {
            log.warn("Authentication failed for trainee username {}", username);
            throw new AuthenticationException("Invalid username or password");
        }
        return trainee;
    }

    public Trainer authenticateTrainer(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        if (!trainer.getUser().getPassword().equals(password)) {
            log.warn("Authentication failed for trainer username {}", username);
            throw new AuthenticationException("Invalid username or password");
        }
        return trainer;
    }
}