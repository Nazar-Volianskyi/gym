package com.epam.gym.dao;

import com.epam.gym.model.Training;
import com.epam.gym.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingDaoImplTest {

    private TrainingDaoImpl trainingDao;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
        trainingDao = new TrainingDaoImpl();
        trainingDao.setTrainingStorage(trainingStorage);
    }

    @Test
    void create_shouldStoreTrainingInStorage() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Cardio");
        Training result = trainingDao.create(training);

        assertEquals(training, result);
        assertTrue(trainingStorage.getStorage().containsKey(1L));
    }

    @Test
    void findById_shouldReturnTraining_whenExists() {
        Training training = new Training();
        training.setId(1L);
        trainingDao.create(training);
        Optional<Training> result = trainingDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Training> result = trainingDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllStoredTrainings() {
        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        trainingDao.create(training1);
        trainingDao.create(training2);
        List<Training> result = trainingDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void nextId_shouldReturnIncrementingValues() {
        Long first = trainingDao.nextId();
        Long second = trainingDao.nextId();

        assertEquals(first + 1, second);
    }
}