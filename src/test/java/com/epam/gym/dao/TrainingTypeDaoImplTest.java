package com.epam.gym.dao;

import com.epam.gym.model.TrainingType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class TrainingTypeDaoImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByName_shouldReturnTrainingType_whenExists() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");
        entityManager.persist(cardio);
        entityManager.flush();
        entityManager.clear();

        Optional<TrainingType> result = trainingTypeDao.findByName("Cardio");

        assertTrue(result.isPresent());
        assertEquals("Cardio", result.get().getTrainingTypeName());
    }

    @Test
    void findByName_shouldReturnEmpty_whenNotExists() {
        Optional<TrainingType> result = trainingTypeDao.findByName("Noname");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllTrainingTypes() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");
        TrainingType strength = new TrainingType();
        strength.setTrainingTypeName("Strength");
        entityManager.persist(cardio);
        entityManager.persist(strength);
        entityManager.flush();
        entityManager.clear();

        List<TrainingType> result = trainingTypeDao.findAll();

        assertEquals(2, result.size());
    }
}