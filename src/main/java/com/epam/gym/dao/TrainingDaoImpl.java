package com.epam.gym.dao;

import com.epam.gym.model.Training;
import com.epam.gym.storage.TrainingStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training create(Training training) {
        trainingStorage.getStorage().put(training.getId(), training);
        log.info("Training created with id {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("Fetching training by id {}", id);
        Optional<Training> result = Optional.ofNullable(trainingStorage.getStorage().get(id));
        if (result.isEmpty()) {
            log.warn("Training with id {} not found", id);
        }
        return result;
    }

    @Override
    public List<Training> findAll() {
        log.debug("Fetching all trainings, count: {}", trainingStorage.getStorage().size());
        return List.copyOf(trainingStorage.getStorage().values());
    }

    @Override
    public Long nextId() {
        return trainingStorage.nextId();
    }
}