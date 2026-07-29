package com.epam.gym.facade;

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
        log.debug("Facade: creating trainee");
        return traineeService.create(trainee);
    }

    public Trainee getTraineeProfile(String username, String password) {
        log.debug("Facade: fetching trainee profile for username {}", username);
        return traineeService.findByUsername(username, password);
    }

    public void changeTraineePassword(String username, String password, String newPassword) {
        log.debug("Facade: changing password for trainee username {}", username);
        traineeService.changePassword(username, password, newPassword);
    }

    public Trainee updateTraineeProfile(String username, String password, Trainee updatedData) {
        log.debug("Facade: updating trainee profile for username {}", username);
        return traineeService.update(username, password, updatedData);
    }

    public void setTraineeActiveStatus(String username, String password, boolean isActive) {
        log.debug("Facade: setting active status {} for trainee username {}", isActive, username);
        traineeService.setActiveStatus(username, password, isActive);
    }

    public void deleteTraineeProfile(String username, String password) {
        log.debug("Facade: deleting trainee profile with username {}", username);
        traineeService.delete(username, password);
    }

    public List<Training> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                              String trainingTypeName) {
        log.debug("Facade: fetching trainings for trainee username {}", username);
        return traineeService.getTrainings(username, password, fromDate, toDate, trainingTypeName);
    }

    public List<Trainer> getUnassignedTrainers(String username, String password) {
        log.debug("Facade: fetching unassigned trainers for trainee username {}", username);
        return traineeService.getUnassignedTrainers(username, password);
    }

    public Trainee updateTraineeTrainersList(String username, String password, List<Long> trainerIds) {
        log.debug("Facade: updating trainers list for trainee username {}", username);
        return traineeService.updateTrainersList(username, password, trainerIds);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade: creating trainer");
        return trainerService.create(trainer);
    }

    public Trainer getTrainerProfile(String username, String password) {
        log.debug("Facade: fetching trainer profile for username {}", username);
        return trainerService.findByUsername(username, password);
    }

    public void changeTrainerPassword(String username, String password, String newPassword) {
        log.debug("Facade: changing password for trainer username {}", username);
        trainerService.changePassword(username, password, newPassword);
    }

    public Trainer updateTrainerProfile(String username, String password, Trainer updatedData) {
        log.debug("Facade: updating trainer profile for username {}", username);
        return trainerService.update(username, password, updatedData);
    }

    public void setTrainerActiveStatus(String username, String password, boolean isActive) {
        log.debug("Facade: setting active status {} for trainer username {}", isActive, username);
        trainerService.setActiveStatus(username, password, isActive);
    }

    public List<Training> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate){
        log.debug("Facade: fetching trainings for trainer username {}", username);
        return trainerService.getTrainings(username, password, fromDate, toDate);
    }

    public Training addTraining(String trainerUsername, String trainerPassword, Training training) {
        log.debug("Facade: adding training");
        return trainingService.addTraining(trainerUsername, trainerPassword, training);
    }
}