package com.epam.gym.dao;

import com.epam.gym.model.Trainee;
import com.epam.gym.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private TraineeStorage traineeStorage;


    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        traineeDao = new TraineeDaoImpl();
        traineeDao.setTraineeStorage(traineeStorage);
    }

    @Test
    void create_shouldStoreTraineeInStorage() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("Nazar");

        Trainee result = traineeDao.create(trainee);
        assertEquals(trainee, result);
        assertTrue(traineeStorage.getStorage().containsKey(1L));
    }

    @Test
    void update_shouldOverwriteExistingTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setAddress("Kyiv");
        traineeDao.create(trainee);

        trainee.setAddress("Lviv");
        traineeDao.update(trainee);

        assertEquals("Lviv", traineeStorage.getStorage().get(1L).getAddress());
    }

    @Test
    void delete_shouldRemoveTraineeFromStorage() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        traineeDao.create(trainee);
        traineeDao.delete(1L);

        assertFalse(traineeStorage.getStorage().containsKey(1L));
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        traineeDao.create(trainee);

        Optional<Trainee> result = traineeDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainee> result = traineeDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllStoredTrainees() {
        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        traineeDao.create(trainee1);
        traineeDao.create(trainee2);
        List<Trainee> result = traineeDao.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void nextId_shouldReturnIncrementingValues() {
        Long first = traineeDao.nextId();
        Long second = traineeDao.nextId();

        assertEquals(first + 1, second);
    }


}
