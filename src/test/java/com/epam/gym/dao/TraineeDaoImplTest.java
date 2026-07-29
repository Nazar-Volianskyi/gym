package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class TraineeDaoImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private EntityManager entityManager;

    private Trainee buildTrainee(String firstName, String lastName, String username) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword("password123");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2005, 10, 27));
        trainee.setAddress("Kyiv");
        return trainee;
    }

    @Test
    void create_shouldPersistTraineeInDatabase() {
        Trainee trainee = buildTrainee("Nazar", "Volianskyi", "Nazar.Volianskyi");

        Trainee created = traineeDao.create(trainee);
        entityManager.flush();

        assertTrue(created.getId() > 0);
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Trainee trainee = buildTrainee("Mykola", "Volianskyi", "Mykola.Volianskyi");
        traineeDao.create(trainee);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> result = traineeDao.findById(trainee.getId());

        assertTrue(result.isPresent());
        assertEquals("Mykola.Volianskyi", result.get().getUser().getUsername());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainee> result = traineeDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_shouldReturnTrainee_whenExists() {
        Trainee trainee = buildTrainee("Mark", "Dok", "Mark.Dok");
        traineeDao.create(trainee);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> result = traineeDao.findByUsername("Mark.Dok");

        assertTrue(result.isPresent());
        assertEquals("Mark", result.get().getUser().getFirstName());
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<Trainee> result = traineeDao.findByUsername("Noname.Noname");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_shouldChangeTraineeData() {
        Trainee trainee = buildTrainee("Oleh", "Petryk", "Oleh.Petryk");
        traineeDao.create(trainee);
        entityManager.flush();
        entityManager.clear();

        Trainee toUpdate = traineeDao.findById(trainee.getId()).orElseThrow();
        toUpdate.setAddress("Lviv");
        traineeDao.update(toUpdate);
        entityManager.flush();
        entityManager.clear();

        Trainee updated = traineeDao.findById(trainee.getId()).orElseThrow();
        assertEquals("Lviv", updated.getAddress());
    }

    @Test
    void delete_shouldRemoveTraineeFromDatabase() {
        Trainee trainee = buildTrainee("Petro", "Volianskyi", "Petro.Volianskyi");
        traineeDao.create(trainee);
        entityManager.flush();
        Long id = trainee.getId();

        traineeDao.delete(trainee);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> result = traineeDao.findById(id);
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        traineeDao.create(buildTrainee("Nazar", "Volianskyi", "Nazar.Volianskyi"));
        traineeDao.create(buildTrainee("Ihor", "Volianskyi", "Ihor.Volianskyi"));
        entityManager.flush();
        entityManager.clear();
        List<Trainee> result = traineeDao.findAll();

        assertEquals(2, result.size());
    }
}