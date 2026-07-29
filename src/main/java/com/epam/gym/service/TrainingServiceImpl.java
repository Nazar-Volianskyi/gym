package com.epam.gym.service;

import com.epam.gym.dao.TrainingDao;
import com.epam.gym.model.Training;
import com.epam.gym.util.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public Training addTraining(String trainerUsername, String trainerPassword, Training training) {
        authenticationService.authenticateTrainer(trainerUsername, trainerPassword);
        Training created = trainingDao.create(training);
        log.info("Training added with id {}", created.getId());
        return created;
    }
}