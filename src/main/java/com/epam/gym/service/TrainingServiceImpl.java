package com.epam.gym.service;

import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Training create(Training training) {
        training.setId(trainingDao.nextId());
        log.info("Creating training with id {}", training.getId());
        return trainingDao.create(training);
    }

    @Override
    public Training findById(Long id) {
        log.debug("Fetching training by id {}", id);
        return trainingDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training", id));
    }

    @Override
    public List<Training> findAll() {
        log.debug("Fetching all trainings");
        return trainingDao.findAll();
    }
}
