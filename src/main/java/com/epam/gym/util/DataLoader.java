package com.epam.gym.util;


import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.storage.TraineeStorage;
import com.epam.gym.storage.TrainerStorage;
import com.epam.gym.storage.TrainingStorage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.IOException;

@Component
@Slf4j
public class DataLoader {

    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;
    private ResourceLoader resourceLoader;

    @Value("${data.file.path}")
    private String dataFilePath;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadData(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Resource resource = resourceLoader.getResource(dataFilePath);

        try (InputStream is = resource.getInputStream()){
            JsonNode root = objectMapper.readTree(is);

            long maxTraineeId = 0;
            for (JsonNode node : root.withArray("trainees")) {
                Trainee trainee = objectMapper.treeToValue(node, Trainee.class);
                traineeStorage.getStorage().put(trainee.getId(), trainee);
                maxTraineeId = Math.max(maxTraineeId, trainee.getId());
            }
            traineeStorage.setIdSequenceStart(maxTraineeId + 1);

            long maxTrainerId = 0;
            for (JsonNode node : root.withArray("trainers")) {
                Trainer trainer = objectMapper.treeToValue(node, Trainer.class);
                trainerStorage.getStorage().put(trainer.getId(), trainer);
                maxTrainerId = Math.max(maxTrainerId, trainer.getId());
            }
            trainerStorage.setIdSequenceStart(maxTrainerId + 1);

            long maxTrainingId = 0;
            for (JsonNode node : root.withArray("trainings")) {
                Training training = objectMapper.treeToValue(node, Training.class);
                trainingStorage.getStorage().put(training.getId(), training);
                maxTrainingId = Math.max(maxTrainingId, training.getId());
            }
            trainingStorage.setIdSequenceStart(maxTrainingId + 1);

            log.info("Initial data loaded: {} trainees, {} trainers, {} trainings",
                    traineeStorage.getStorage().size(),
                    trainerStorage.getStorage().size(),
                    trainingStorage.getStorage().size());

        } catch (IOException e) {
            log.error("Failed to load data from file: {}", dataFilePath, e);
        }
    }
}
