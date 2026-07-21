package com.epam.gym.dao;

import com.epam.gym.model.Trainer;
import com.epam.gym.storage.TrainerStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        trainerStorage.getStorage().put(trainer.getId(), trainer);
        log.info("Trainer created with id {}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        trainerStorage.getStorage().put(trainer.getId(), trainer);
        log.info("Trainer updated with id {}", trainer.getId());
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.debug("Fetching trainer by id {}", id);
        Optional<Trainer> result = Optional.ofNullable(trainerStorage.getStorage().get(id));
        if (result.isEmpty()) {
            log.warn("Trainer with id {} not found", id);
        }
        return result;
    }

    @Override
    public List<Trainer> findAll() {
        log.debug("Fetching all trainers, count {}", trainerStorage.getStorage().size());
        return List.copyOf(trainerStorage.getStorage().values());
    }

    @Override
    public Long nextId() {
        return trainerStorage.nextId();
    }
}