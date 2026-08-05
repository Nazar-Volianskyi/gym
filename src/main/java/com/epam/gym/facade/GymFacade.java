package com.epam.gym.facade;

import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.service.TraineeService;
import com.epam.gym.service.TrainerService;
import com.epam.gym.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Trainee createTrainee(Trainee trainee) {
        log.info("Facade: creating trainee");
        return traineeService.create(trainee);
    }

    public Trainee getTraineeProfile(String username) {
        log.info("Facade: fetching trainee profile for username {}", username);
        return traineeService.findByUsername(username);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changing password for trainee username {}", username);
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee updateTraineeProfile(String username, Trainee updatedData, boolean isActive) {
        log.info("Facade: updating trainee profile for username {}", username);
        return traineeService.update(username, updatedData, isActive);
    }

    public void setTraineeActiveStatus(String username, boolean isActive) {
        log.info("Facade: setting active status {} for trainee username {}", isActive, username);
        traineeService.setActiveStatus(username, isActive);
    }

    public void deleteTraineeProfile(String username) {
        log.info("Facade: deleting trainee profile with username {}", username);
        traineeService.delete(username);
    }

    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingTypeName) {
        log.info("Facade: fetching trainings for trainee username {}", username);
        return traineeService.getTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Trainer> getUnassignedTrainers(String username) {
        log.info("Facade: fetching unassigned trainers for trainee username {}", username);
        return traineeService.getUnassignedTrainers(username);
    }

    public Trainee updateTraineeTrainersList(String username, List<String> trainerUsernames) {
        log.info("Facade: updating trainers list for trainee username {}", username);
        return traineeService.updateTrainersList(username, trainerUsernames);
    }

    public Trainer createTrainer(String firstName, String lastName, String specializationName) {
        log.info("Facade: creating trainer");
        return trainerService.create(firstName, lastName, specializationName);
    }

    public Trainer getTrainerProfile(String username) {
        log.info("Facade: fetching trainer profile for username {}", username);
        return trainerService.findByUsername(username);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changing password for trainer username {}", username);
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public Trainer updateTrainerProfile(String username, Trainer updatedData, boolean isActive) {
        log.info("Facade: updating trainer profile for username {}", username);
        return trainerService.update(username, updatedData, isActive);
    }

    public void setTrainerActiveStatus(String username, boolean isActive) {
        log.info("Facade: setting active status {} for trainer username {}", isActive, username);
        trainerService.setActiveStatus(username, isActive);
    }

    public List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        log.info("Facade: fetching trainings for trainer username {}", username);
        return trainerService.getTrainings(username, fromDate, toDate, traineeName);
    }

    public Training addTraining(String traineeUsername, String trainerUsername, String trainingName,
                                LocalDate trainingDate, int trainingDuration) {
        log.info("Facade: adding training");
        return trainingService.addTraining(traineeUsername, trainerUsername, trainingName,
                trainingDate, trainingDuration);
    }

    public void changeLogin(String username, String oldPassword, String newPassword) {
        if (isTrainee(username)) {
            traineeService.changePassword(username, oldPassword, newPassword);
        } else {
            trainerService.changePassword(username, oldPassword, newPassword);
        }
    }

    private boolean isTrainee(String username) {
        try {
            traineeService.findByUsername(username);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
