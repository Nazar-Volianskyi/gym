package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class TrainingDaoImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private EntityManager entityManager;

    private Trainee persistTrainee(String username) {
        User user = new User();
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        user.setUsername(username);
        user.setPassword("password123");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2005, 10, 27));
        trainee.setAddress("Lviv");
        entityManager.persist(trainee);
        return trainee;
    }

    private Trainer persistTrainer(String username, TrainingType specialization) {
        User user = new User();
        user.setFirstName("Mark");
        user.setLastName("Dok");
        user.setUsername(username);
        user.setPassword("password123");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        entityManager.persist(trainer);
        return trainer;
    }

    private TrainingType persistTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        entityManager.persist(type);
        return type;
    }

    private Training buildTraining(Trainee trainee, Trainer trainer, TrainingType type, LocalDate date) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Session");
        training.setTrainingType(type);
        training.setTrainingDate(date);
        training.setTrainingDuration(60);
        return training;
    }

    @Test
    void create_shouldPersistTrainingInDatabase() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);

        Training training = buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 1, 15));
        Training created = trainingDao.create(training);
        entityManager.flush();

        assertTrue(created.getId() > 0);
    }

    @Test
    void findById_shouldReturnTraining_whenExists() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);
        Training training = buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 1, 15));
        trainingDao.create(training);
        entityManager.flush();
        entityManager.clear();

        Optional<Training> result = trainingDao.findById(training.getId());

        assertTrue(result.isPresent());
        assertEquals("Session", result.get().getTrainingName());
    }

    @Test
    void findTraineeTrainings_shouldFilterByDateRange() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);

        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 1, 1)));
        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 6, 1)));
        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 12, 1)));
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDao.findTraineeTrainings(
                "Nazar.Volianskyi", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 9, 1), null);

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 6, 1), result.get(0).getTrainingDate());
    }

    @Test
    void findTraineeTrainings_shouldFilterByTrainingType() {
        TrainingType cardio = persistTrainingType("Cardio");
        TrainingType strength = persistTrainingType("Strength");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);

        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 1, 1)));
        trainingDao.create(buildTraining(trainee, trainer, strength, LocalDate.of(2026, 1, 2)));
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDao.findTraineeTrainings(
                "Nazar.Volianskyi", null, null, "Strength");
        assertEquals(1, result.size());
        assertEquals("Strength", result.get(0).getTrainingType().getTrainingTypeName());
    }

    @Test
    void findTrainerTrainings_shouldFilterByDateRange() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);
        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 2, 1)));
        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 8, 1)));
        entityManager.flush();
        entityManager.clear();
        List<Training> result = trainingDao.findTrainerTrainings(
                "Mark.Dok", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 1));

        assertEquals(1, result.size());
    }

    @Test
    void findAll_shouldReturnAllTrainings() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainee trainee = persistTrainee("Nazar.Volianskyi");
        Trainer trainer = persistTrainer("Mark.Dok", cardio);

        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 1, 1)));
        trainingDao.create(buildTraining(trainee, trainer, cardio, LocalDate.of(2026, 2, 1)));
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDao.findAll();

        assertEquals(2, result.size());
    }
}