package com.epam.gym.util;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsernameUniquenessChecker {

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

    public boolean exists(String username) {
        boolean inTrainees = traineeDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        boolean inTrainers = trainerDao.findAll().stream()
                .anyMatch(t -> t.getUsername().equals(username));
        return inTrainees || inTrainers;
    }
}
