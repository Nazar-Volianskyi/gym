package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.storage.TraineeStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        traineeStorage.getStorage().put(trainee.getId(), trainee);
        log.info("Trainee created with id {}", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        traineeStorage.getStorage().put(trainee.getId(), trainee);
        log.info("Trainee updated with id {}", trainee.getId());
        return trainee;
    }

    @Override
    public void delete(Long id) {
        traineeStorage.getStorage().remove(id);
        log.info("Trainee deleted with id {}", id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.debug("Fetching trainee with id {}", id);
        Optional<Trainee> result = Optional.ofNullable(traineeStorage.getStorage().get(id));
        if (result.isEmpty()) {
            log.warn("Trainee with id {} not found", id);
        }
        return result;
    }

    @Override
    public List<Trainee> findAll() {
        log.debug("Fetching all trainees, count: {}", traineeStorage.getStorage().size());
        return List.copyOf(traineeStorage.getStorage().values());
    }

    @Override
    public Long nextId() {
        return traineeStorage.nextId();
    }
}