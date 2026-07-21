package com.epam.gym;

import com.epam.gym.config.AppConfig;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

@Slf4j
public class App {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);

        printInitialData(facade);

        Trainee trainee = createTrainee(facade);
        Trainer trainer = createTrainer(facade);
        createTraining(facade, trainee, trainer);
    }

    private static void printInitialData(GymFacade facade) {
        log.info("Trainees: {}", facade.getAllTrainees());
        log.info("Trainers: {}", facade.getAllTrainers());
        log.info("Trainings: {}", facade.getAllTrainings());
    }

    private static Trainee createTrainee(GymFacade facade) {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Nazar");
        trainee.setLastName("Volianskyi");
        trainee.setDateOfBirth(LocalDate.of(2005, 5, 27));
        trainee.setAddress("Lviv");

        Trainee savedTrainee = facade.createTrainee(trainee);
        log.info("Created trainee: {}", savedTrainee);

        savedTrainee.setAddress("Kyiv");
        Trainee updatedTrainee = facade.updateTrainee(savedTrainee);
        log.info("Updated trainee: {}", updatedTrainee);
        return updatedTrainee;
    }

    private static Trainer createTrainer(GymFacade facade) {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Mykola");
        trainer.setLastName("Tymchuk");
        trainer.setSpecialization(new TrainingType(1L, "Cardio"));

        Trainer savedTrainer = facade.createTrainer(trainer);
        log.info("Created trainer: {}", savedTrainer);
        return savedTrainer;
    }

    private static void createTraining(GymFacade facade, Trainee trainee, Trainer trainer) {
        Training training = new Training();
        training.setTraineeId(trainee.getId());
        training.setTrainerId(trainer.getId());
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(new TrainingType(1L, "Cardio"));
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        Training savedTraining = facade.createTraining(training);
        log.info("Created training: {}", savedTraining);
    }
}
