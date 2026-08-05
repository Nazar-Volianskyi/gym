package com.epam.gym.service;

import com.epam.gym.dao.TraineeDao;
import com.epam.gym.dao.TrainerDao;
import com.epam.gym.dao.TrainingDao;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
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
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
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

    @Override
    @Transactional
    public Trainee create(Trainee trainee) {
        String firstName = trainee.getUser().getFirstName();
        String lastName = trainee.getUser().getLastName();
        if (trainerDao.existsByFullName(firstName, lastName)) {
            throw new ConflictException(
                    "%s %s is already registered as a trainer".formatted(firstName, lastName));
        }
        userProfileInitializer.initialize(trainee.getUser());
        Trainee created = traineeDao.create(trainee);
        log.info("Created trainee profile with username {}", created.getUser().getUsername());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findByUsername(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee", username));
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = authenticationService.authenticateTrainee(username, oldPassword);
        trainee.getUser().setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Password changed for trainee username {}", username);
    }

    @Override
    @Transactional
    public Trainee update(String username, Trainee updatedData, boolean isActive) {
        Trainee trainee = findByUsername(username);
        trainee.getUser().setFirstName(updatedData.getUser().getFirstName());
        trainee.getUser().setLastName(updatedData.getUser().getLastName());
        trainee.setDateOfBirth(updatedData.getDateOfBirth());
        trainee.setAddress(updatedData.getAddress());
        trainee.getUser().setActive(isActive);
        Trainee updated = traineeDao.update(trainee);
        log.info("Updated trainee profile for username {}", username);
        return updated;
    }

    @Override
    @Transactional
    public void setActiveStatus(String username, boolean isActive) {
        Trainee trainee = findByUsername(username);
        if (trainee.getUser().isActive() == isActive) {
            throw new ConflictException(
                    "Trainee %s is already %s".formatted(username, isActive ? "active" : "inactive"));
        }
        trainee.getUser().setActive(isActive);
        traineeDao.update(trainee);
        log.info("Trainee {} status changed to {}", username, isActive ? "active" : "inactive");
    }

    @Override
    @Transactional
    public void delete(String username) {
        Trainee trainee = findByUsername(username);
        traineeDao.delete(trainee);
        log.info("Deleted trainee profile with username {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                       String trainerName, String trainingTypeName) {
        findByUsername(username);
        return trainingDao.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username) {
        findByUsername(username);
        return trainerDao.findTrainersNotAssignedToTrainee(username);
    }

    @Override
    @Transactional
    public Trainee updateTrainersList(String username, List<String> trainerUsernames) {
        Trainee trainee = findByUsername(username);
        List<Trainer> trainers = trainerUsernames.stream()
                .map(trainerUsername -> trainerDao.findByUsername(trainerUsername)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer", trainerUsername)))
                .toList();
        trainee.setTrainers(trainers);
        Trainee updated = traineeDao.update(trainee);
        log.info("Updated trainers list for trainee username {}", username);
        return updated;
    }
}
