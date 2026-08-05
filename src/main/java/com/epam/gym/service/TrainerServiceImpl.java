package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.dao.TrainingTypeDao;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
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

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
    private TrainingTypeDao trainingTypeDao;
    private UserProfileInitializer userProfileInitializer;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

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

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    @Transactional
    public Trainer create(String firstName, String lastName, String specializationName) {
        if (traineeDao.existsByFullName(firstName, lastName)) {
            throw new ConflictException(
                    "%s %s is already registered as a trainee".formatted(firstName, lastName));
        }
        TrainingType specialization = trainingTypeDao.findByName(specializationName)
                        .orElseThrow(() -> new EntityNotFoundException("TrainingType", specializationName));

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        userProfileInitializer.initialize(trainer.getUser());

        Trainer created = trainerDao.create(trainer);
        log.info("Created trainer profile with username {}", created.getUser().getUsername());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username) {
        log.info("Fetching trainer profile for username {}", username);
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer", username));
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = authenticationService.authenticateTrainer(username, oldPassword);
        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Password changed for trainer username {}", username);
    }

    @Override
    @Transactional
    public Trainer update(String username, Trainer updatedData, boolean isActive) {
        Trainer trainer = findByUsername(username);
        trainer.getUser().setFirstName(updatedData.getUser().getFirstName());
        trainer.getUser().setLastName(updatedData.getUser().getLastName());
        trainer.getUser().setActive(isActive);
        Trainer updated = trainerDao.update(trainer);
        log.info("Updated trainer profile for username {}", username);
        return updated;
    }

    @Override
    @Transactional
    public void setActiveStatus(String username, boolean isActive) {
        Trainer trainer = findByUsername(username);
        if (trainer.getUser().isActive() == isActive) {
            throw new ConflictException(
                    "Trainer %s is already %s".formatted(username, isActive ? "active" : "inactive"));
        }
        trainer.getUser().setActive(isActive);
        trainerDao.update(trainer);
        log.info("Trainer {} status changed to {}", username, isActive ? "active" : "inactive");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        findByUsername(username);
        return trainingDao.findTrainerTrainings(username, fromDate, toDate, traineeName);
    }
}
