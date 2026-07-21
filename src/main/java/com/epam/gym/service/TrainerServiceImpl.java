package com.epam.gym.service;

import com.epam.gym.dao.TrainerDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainer;
import com.epam.gym.util.UserProfileInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private UserProfileInitializer userProfileInitializer;


    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUserProfileInitializer(UserProfileInitializer userProfileInitializer) {
        this.userProfileInitializer = userProfileInitializer;
    }


    @Override
    public Trainer create(Trainer trainer) {
        trainer.setId(trainerDao.nextId());
        userProfileInitializer.initialize(trainer);
        log.info("Creating trainer with generated username {}", trainer.getUsername());
        return trainerDao.create(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        log.info("Updating trainer with id {}", trainer.getId());
        findById(trainer.getId());
        return trainerDao.update(trainer);
    }

    @Override
    public Trainer findById(Long id) {
        log.debug("Fetching trainer by id {}", id);
        return trainerDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", id));
    }

    @Override
    public List<Trainer> findAll() {
        log.debug("Fetching all trainers");
        return trainerDao.findAll();
    }
}
