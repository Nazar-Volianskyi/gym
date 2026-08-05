package com.epam.gym.controller;


import com.epam.gym.dto.mapper.TraineeMapper;
import com.epam.gym.dto.mapper.TrainingMapper;
import com.epam.gym.dto.request.ActiveStatusRequest;
import com.epam.gym.dto.request.TraineeRegistrationRequest;
import com.epam.gym.dto.request.UpdateTraineeRequest;
import com.epam.gym.dto.request.UpdateTraineeTrainersRequest;
import com.epam.gym.dto.response.RegistrationResponse;
import com.epam.gym.dto.response.TraineeProfileResponse;
import com.epam.gym.dto.response.TraineeTrainingResponse;
import com.epam.gym.dto.response.TrainerInfoResponse;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.Training;
import com.epam.gym.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@Slf4j
@Tag(name = "Trainees", description = "Trainee registration, profile management and trainings")
public class TraineeController {

    private GymFacade gymFacade;
    private TraineeMapper traineeMapper;
    private TrainingMapper trainingMapper;

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Operation(summary = "Register a new trainee", description = "Creates a trainee profile and generates " +
            "a unique username and password. Publicly accessible, no authentication required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Same person already registered as a trainer")
    })
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
        log.info("Registering new trainee");

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        Trainee created = gymFacade.createTrainee(trainee);

        RegistrationResponse response = new RegistrationResponse(
                created.getUser().getUsername(), created.getUser().getPassword());

        log.info("Trainee registered with username {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Get trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @Parameter(description = "Trainee's username", required = true) @PathVariable String username) {
        log.info("Fetching trainee profile for username {}", username);
        Trainee trainee = gymFacade.getTraineeProfile(username);
        return ResponseEntity.ok(traineeMapper.toResponse(trainee));
    }

    @Operation(summary = "Update trainee profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping
    public ResponseEntity<TraineeProfileResponse> updateProfile(@Valid @RequestBody UpdateTraineeRequest request) {
        log.info("Updating trainee profile for username {}", request.getUsername());

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainee updatedData = new Trainee();
        updatedData.setUser(user);
        updatedData.setDateOfBirth(request.getDateOfBirth());
        updatedData.setAddress(request.getAddress());

        Trainee updated = gymFacade.updateTraineeProfile(
                request.getUsername(), updatedData, request.getActive());

        return ResponseEntity.ok(traineeMapper.toResponse(updated));
    }

    @Operation(summary = "Delete trainee profile", description = "Hard delete - also cascades deletion of " +
            "the trainee's trainings.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "Trainee's username", required = true) @PathVariable String username) {
        log.info("Deleting trainee profile with username {}", username);
        gymFacade.deleteTraineeProfile(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get trainers not assigned to a trainee", description = "Returns active trainers " +
            "that are not currently assigned to the given trainee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerInfoResponse>> getUnassignedTrainers(
            @Parameter(description = "Trainee's username", required = true) @PathVariable String username) {
        log.info("Fetching unassigned active trainers for trainee username {}", username);
        List<Trainer> trainers = gymFacade.getUnassignedTrainers(username);
        return ResponseEntity.ok(traineeMapper.mapTrainers(trainers));
    }

    @Operation(summary = "Update trainee's trainer list", description = "Replaces the full list of trainers " +
            "assigned to the trainee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Trainee or one of the trainers not found")
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerInfoResponse>> updateTrainersList(
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        log.info("Updating trainers list for trainee username {}", request.getTraineeUsername());
        Trainee updated = gymFacade.updateTraineeTrainersList(
                request.getTraineeUsername(), request.getTrainerUsernames());
        return ResponseEntity.ok(traineeMapper.mapTrainers(updated.getTrainers()));
    }

    @Operation(summary = "Get trainee's trainings list", description = "Returns the trainee's trainings, " +
            "optionally filtered by period, trainer name and training type.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @Parameter(description = "Trainee's username", required = true)
            @PathVariable String username,
            @Parameter(description = "Start of the period (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @Parameter(description = "End of the period (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @Parameter(description = "Filter by trainer username")
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter by training type name")
            @RequestParam(required = false) String trainingType) {
        log.info("Fetching trainings for trainee username {}", username);
        List<Training> trainings = gymFacade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);
        return ResponseEntity.ok(trainings.stream().map(trainingMapper::toTraineeTrainingResponse).toList());
    }

    @Operation(summary = "Activate or deactivate a trainee", description = "Not idempotent - calling it with " +
            "the status the trainee already has results in an error.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "409", description = "Trainee already has the requested status")
    })
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> setActiveStatus(
            @Parameter(description = "Trainee's username", required = true) @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        log.info("Setting active status {} for trainee username {}", request.getActive(), username);
        gymFacade.setTraineeActiveStatus(username, request.getActive());
        return ResponseEntity.ok().build();
    }
}
