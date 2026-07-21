package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
import com.epam.gym.util.UserProfileInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private UserProfileInitializer userProfileInitializer;


    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserProfileInitializer(UserProfileInitializer userProfileInitializer) {
        this.userProfileInitializer = userProfileInitializer;
    }

    @Override
    public Trainee create(Trainee trainee) {
        trainee.setId(traineeDao.nextId());
        userProfileInitializer.initialize(trainee);
        log.info("Creating trainee with generated username {}", trainee.getUsername());
        return traineeDao.create(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        log.info("Updating trainee with id {}", trainee.getId());
        findById(trainee.getId());
        return traineeDao.update(trainee);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting trainee with id {}", id);
        findById(id);
        traineeDao.delete(id);
    }

    @Override
    public Trainee findById(Long id) {
        log.debug("Fetching trainee by id {}", id);
        return traineeDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", id));
    }

    @Override
    public List<Trainee> findAll() {
        log.debug("Fetching all trainees");
        return traineeDao.findAll();
    }
}
