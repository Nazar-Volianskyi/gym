package com.epam.gym.util;

import com.epam.gym.storage.TraineeStorage;
import com.epam.gym.storage.TrainerStorage;
import com.epam.gym.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataLoaderTest {

    private DataLoader dataLoader;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new TraineeStorage();
        trainerStorage = new TrainerStorage();
        trainingStorage = new TrainingStorage();
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        dataLoader = new DataLoader();
        dataLoader.setTraineeStorage(traineeStorage);
        dataLoader.setTrainerStorage(trainerStorage);
        dataLoader.setTrainingStorage(trainingStorage);
        dataLoader.setResourceLoader(resourceLoader);
        ReflectionTestUtils.setField(dataLoader, "dataFilePath", "classpath:test-data.json");
    }

    @Test
    void loadData_shouldPopulateAllStorages() {
        dataLoader.loadData();

        assertEquals(2, traineeStorage.getStorage().size());
        assertEquals(1, trainerStorage.getStorage().size());
        assertEquals(1, trainingStorage.getStorage().size());
    }

    @Test
    void loadData_shouldSetIdSequenceStart_basedOnMaxLoadedId() {
        dataLoader.loadData();

        Long nextTraineeId = traineeStorage.nextId();

        assertEquals(6L, nextTraineeId);
    }

}