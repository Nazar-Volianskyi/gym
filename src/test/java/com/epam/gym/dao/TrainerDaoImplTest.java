package com.epam.gym.dao;

import com.epam.gym.model.Trainer;
import com.epam.gym.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;
    private TrainerStorage trainerStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new TrainerStorage();
        trainerDao = new TrainerDaoImpl();
        trainerDao.setTrainerStorage(trainerStorage);
    }

    @Test
    void create_shouldStoreTrainerInStorage() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Nazar");
        Trainer result = trainerDao.create(trainer);

        assertEquals(trainer, result);
        assertTrue(trainerStorage.getStorage().containsKey(1L));
    }

    @Test
    void update_shouldOverwriteExistingTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Nazar");
        trainerDao.create(trainer);
        trainer.setFirstName("Ihor");
        trainerDao.update(trainer);

        assertEquals("Ihor", trainerStorage.getStorage().get(1L).getFirstName());
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainerDao.create(trainer);
        Optional<Trainer> result = trainerDao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainer> result = trainerDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllStoredTrainers() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainerDao.create(trainer1);
        trainerDao.create(trainer2);
        List<Trainer> result = trainerDao.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void nextId_shouldReturnIncrementingValues() {
        Long first = trainerDao.nextId();
        Long second = trainerDao.nextId();

        assertEquals(first + 1, second);
    }
}