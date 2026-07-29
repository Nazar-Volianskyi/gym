package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class TrainerDaoImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private EntityManager entityManager;

    private TrainingType persistTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        entityManager.persist(type);
        return type;
    }

    private Trainer buildTrainer(String firstName, String lastName, String username, TrainingType specialization) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password123");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    @Test
    void create_shouldPersistTrainerInDatabase() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainer trainer = buildTrainer("Nazar", "Volianskyi", "Nazar.Volianskyi", cardio);

        Trainer created = trainerDao.create(trainer);
        entityManager.flush();

        assertTrue(created.getId() > 0);
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainer trainer = buildTrainer("Mykola", "Tymchuk", "Mykola.Tymchuk", cardio);
        trainerDao.create(trainer);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> result = trainerDao.findById(trainer.getId());

        assertTrue(result.isPresent());
        assertEquals("Mykola.Tymchuk", result.get().getUser().getUsername());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainer> result = trainerDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_shouldReturnTrainer_whenExists() {
        TrainingType strength = persistTrainingType("Strength");
        Trainer trainer = buildTrainer("Olena", "Bondar", "Olena.Bondar", strength);
        trainerDao.create(trainer);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> result = trainerDao.findByUsername("Olena.Bondar");

        assertTrue(result.isPresent());
        assertEquals("Strength", result.get().getSpecialization().getTrainingTypeName());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<Trainer> result = trainerDao.findByUsername("Noname.Noname");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_shouldChangeTrainerSpecialization() {
        TrainingType cardio = persistTrainingType("Cardio");
        TrainingType yoga = persistTrainingType("Yoga");
        Trainer trainer = buildTrainer("Ihor", "Volianskyi", "Ihor.Volianskyi", cardio);
        trainerDao.create(trainer);
        entityManager.flush();
        entityManager.clear();

        Trainer toUpdate = trainerDao.findById(trainer.getId()).orElseThrow();
        toUpdate.setSpecialization(yoga);
        trainerDao.update(toUpdate);
        entityManager.flush();
        entityManager.clear();

        Trainer updated = trainerDao.findById(trainer.getId()).orElseThrow();
        assertEquals("Yoga", updated.getSpecialization().getTrainingTypeName());
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        TrainingType cardio = persistTrainingType("Cardio");
        trainerDao.create(buildTrainer("Nazar", "Volianskyi", "Nazar.Volianskyi", cardio));
        trainerDao.create(buildTrainer("Ihor", "Volianskyi", "Ihor.Volianskyi", cardio));
        entityManager.flush();
        entityManager.clear();
        List<Trainer> result = trainerDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findTrainersNotAssignedToTrainee_shouldExcludeAssignedTrainers() {
        TrainingType cardio = persistTrainingType("Cardio");
        Trainer assignedTrainer = buildTrainer("Mykola", "Volianskyi", "Mykola.Volianskyi", cardio);
        Trainer unassignedTrainer = buildTrainer("Maria", "Volianska", "Maria.Volianska", cardio);
        entityManager.persist(assignedTrainer);
        entityManager.persist(unassignedTrainer);

        User traineeUser = new User();
        traineeUser.setFirstName("Mark");
        traineeUser.setLastName("Dok");
        traineeUser.setUsername("Mark.Dok");
        traineeUser.setPassword("password123");
        traineeUser.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setDateOfBirth(java.time.LocalDate.of(2005, 10, 27));
        trainee.setAddress("Kyiv");
        trainee.setTrainers(List.of(assignedTrainer));
        entityManager.persist(trainee);

        entityManager.flush();
        entityManager.clear();

        List<Trainer> result = trainerDao.findTrainersNotAssignedToTrainee("Mark.Dok");

        assertEquals(1, result.size());
        assertEquals("Maria.Volianska", result.get(0).getUser().getUsername());
    }
}