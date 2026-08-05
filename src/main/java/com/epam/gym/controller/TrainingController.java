package com.epam.gym.controller;

import com.epam.gym.dao.TrainingTypeDao;
import com.epam.gym.dto.request.AddTrainingRequest;
import com.epam.gym.dto.response.TrainingTypeResponse;
import com.epam.gym.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "Trainings", description = "Training sessions and training types")
public class TrainingController {

    private GymFacade gymFacade;
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Operation(summary = "Add a training session", description = "Training type is derived from the " +
            "trainer's specialization.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PostMapping("/api/trainings")
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        log.info("Adding training '{}' for trainee {} with trainer {}",
                request.getTrainingName(), request.getTraineeUsername(), request.getTrainerUsername());
        gymFacade.addTraining(request.getTraineeUsername(), request.getTrainerUsername(),
                request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get training types", description = "Returns the constant list of training types. " +
            "Cannot be modified through the application.")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping("/api/training-types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        log.info("Fetching training types");
        List<TrainingTypeResponse> response = trainingTypeDao.findAll().stream()
                .map(type -> {
                    TrainingTypeResponse dto = new TrainingTypeResponse();
                    dto.setId(type.getId());
                    dto.setTrainingTypeName(type.getTrainingTypeName());
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(response);
    }
}
