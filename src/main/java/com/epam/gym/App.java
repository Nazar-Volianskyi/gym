package com.epam.gym;

import com.epam.gym.config.AppConfig;
import com.epam.gym.dao.TrainingTypeDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

@Slf4j
public class App {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacade facade = context.getBean(GymFacade.class);
        TrainingTypeDao trainingTypeDao = context.getBean(TrainingTypeDao.class);


        Trainee trainee = createTrainee(facade);
        Trainer trainer = createTrainer(facade, trainingTypeDao);

        String traineeUsername = trainee.getUser().getUsername();
        String traineePassword = trainee.getUser().getPassword();
        String trainerUsername = trainer.getUser().getUsername();
        String trainerPassword = trainer.getUser().getPassword();

        demonstrateProfileOperations(facade, traineeUsername, traineePassword);
        addTraining(facade, trainee, trainer, trainerUsername, trainerPassword);
        demonstrateTrainingsList(facade, traineeUsername, traineePassword);
    }

    private static Trainee createTrainee(GymFacade facade) {
        User user = new User();
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2005, 5, 27));
        trainee.setAddress("Lviv");

        Trainee created = facade.createTrainee(trainee);
        log.info("Created trainee: username={}, password={}", created.getUser().getUsername(), "***hidden***");
        return created;
    }

    private static Trainer createTrainer(GymFacade facade, TrainingTypeDao trainingTypeDao) {
        User user = new User();
        user.setFirstName("Mykola");
        user.setLastName("Volianskyi");

        TrainingType cardio = trainingTypeDao.findByName("Cardio")
                .orElseThrow(() -> new EntityNotFoundException("TrainingType", "Cardio"));

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(cardio);

        Trainer created = facade.createTrainer(trainer);
        log.info("Created trainer: username={}", created.getUser().getUsername());
        return created;
    }

    private static void demonstrateProfileOperations(GymFacade facade, String username, String password) {
        Trainee fetched = facade.getTraineeProfile(username, password);
        log.info("Fetched trainee profile: {}", fetched);

        Trainee update = new Trainee();
        User updatedUser = new User();
        updatedUser.setFirstName(fetched.getUser().getFirstName());
        updatedUser.setLastName(fetched.getUser().getLastName());
        update.setUser(updatedUser);
        update.setAddress("Kyiv");
        update.setDateOfBirth(fetched.getDateOfBirth());

        Trainee updated = facade.updateTraineeProfile(username, password, update);
        log.info("Updated trainee address: {}", updated.getAddress());
        facade.setTraineeActiveStatus(username, password, false);
        log.info("Trainee deactivated");
    }

    private static void addTraining(GymFacade facade, Trainee trainee, Trainer trainer,
                                    String trainerUsername, String trainerPassword) {
        TrainingType cardio = trainer.getSpecialization();
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(cardio);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        Training created = facade.addTraining(trainerUsername, trainerPassword, training);
        log.info("Created training: {}", created);
    }

    private static void demonstrateTrainingsList(GymFacade facade, String username, String password) {
        var trainings = facade.getTraineeTrainings(username, password, null, null, null);
        log.info("Trainee trainings: {}", trainings);
    }
}