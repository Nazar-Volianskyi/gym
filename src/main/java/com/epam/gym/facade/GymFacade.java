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

    public Trainee updateTrainee(Trainee trainee) {
        log.debug("Facade: updating trainee with id {}", trainee.getId());
        return traineeService.update(trainee);
    }

    public void deleteTrainee(Long id) {
        log.debug("Facade: deleting trainee with id {}", id);
        traineeService.delete(id);
    }

    public Trainee getTrainee(Long id) {
        log.debug("Facade: fetching trainee with id {}", id);
        return traineeService.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        log.debug("Facade: fetching all trainees");
        return traineeService.findAll();
    }

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade: creating trainer");
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        log.debug("Facade: updating trainer with id {}", trainer.getId());
        return trainerService.update(trainer);
    }

    public Trainer getTrainer(Long id) {
        log.debug("Facade: fetching trainer with id {}", id);
        return trainerService.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        log.debug("Facade: fetching all trainers");
        return trainerService.findAll();
    }

    public Training createTraining(Training training) {
        log.debug("Facade: creating training");
        return trainingService.create(training);
    }

    public Training getTraining(Long id) {
        log.debug("Facade: fetching training with id {}", id);
        return trainingService.findById(id);
    }

    public List<Training> getAllTrainings() {
        log.debug("Facade: fetching all trainings");
        return trainingService.findAll();
    }
}
