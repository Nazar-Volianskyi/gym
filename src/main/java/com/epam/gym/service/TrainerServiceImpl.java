package com.epam.gym.service;

import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.util.AuthenticationService;
import com.epam.gym.util.UserProfileInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
    private UserProfileInitializer userProfileInitializer;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setUserProfileInitializer(UserProfileInitializer userProfileInitializer) {
        this.userProfileInitializer = userProfileInitializer;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        userProfileInitializer.initialize(trainer.getUser());
        Trainer created = trainerDao.create(trainer);
        log.info("Created trainer profile with username {}", created.getUser().getUsername());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username, String password) {
        log.debug("Fetching trainer profile for username {}", username);
        return authenticationService.authenticateTrainer(username, password);
    }

    @Override
    @Transactional
    public void changePassword(String username, String password, String newPassword) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Password changed for trainer username {}", username);
    }

    @Override
    @Transactional
    public Trainer update(String username, String password, Trainer updatedData) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        trainer.getUser().setFirstName(updatedData.getUser().getFirstName());
        trainer.getUser().setLastName(updatedData.getUser().getLastName());
        trainer.setSpecialization(updatedData.getSpecialization());
        Trainer updated = trainerDao.update(trainer);
        log.info("Updated trainer profile for username {}", username);
        return updated;
    }

    @Override
    @Transactional
    public void setActiveStatus(String username, String password, boolean isActive) {
        Trainer trainer = authenticationService.authenticateTrainer(username, password);
        if (trainer.getUser().isActive() == isActive) {
            log.warn("Trainer {} is already {}", username, isActive ? "active" : "inactive");
            return;
        }
        trainer.getUser().setActive(isActive);
        trainerDao.update(trainer);
        log.info("Trainer {} status changed to {}", username, isActive ? "active" : "inactive");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, String password, LocalDate fromDate, LocalDate toDate) {
        authenticationService.authenticateTrainer(username, password);
        return trainingDao.findTrainerTrainings(username, fromDate, toDate);
    }
}
