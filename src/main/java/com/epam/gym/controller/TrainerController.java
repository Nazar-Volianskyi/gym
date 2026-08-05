package com.epam.gym.controller;


import com.epam.gym.dto.mapper.TrainerMapper;
import com.epam.gym.dto.mapper.TrainingMapper;
import com.epam.gym.dto.request.ActiveStatusRequest;
import com.epam.gym.dto.request.TrainerRegistrationRequest;
import com.epam.gym.dto.request.UpdateTrainerRequest;
import com.epam.gym.dto.response.RegistrationResponse;
import com.epam.gym.dto.response.TrainerProfileResponse;
import com.epam.gym.dto.response.TrainerTrainingResponse;
import com.epam.gym.facade.GymFacade;
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
@RequestMapping("/api/trainers")
@Slf4j
@Tag(name = "Trainers", description = "Trainer registration, profile management and trainings")
public class TrainerController {

    private GymFacade gymFacade;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;

    @Autowired
    public void setGymFacade(GymFacade gymFacade){
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Operation(summary = "Register a new trainer", description = "Creates a trainer profile and generates " +
            "a unique username and password. Publicly accessible, no authentication required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Specialization (training type) not found"),
            @ApiResponse(responseCode = "409", description = "Same person already registered as a trainee")
    })
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TrainerRegistrationRequest request){
        log.info("Registering new trainer");

        Trainer created = gymFacade.createTrainer(request.getFirstName(),
                request.getLastName(), request.getSpecialization());

        RegistrationResponse response = new RegistrationResponse(
                created.getUser().getUsername(), created.getUser().getPassword());
        log.info("Trainer registered with username {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get trainer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @Parameter(description = "Trainer's username", required = true) @PathVariable String username) {
        log.info("Fetching trainer profile for username {}", username);
        Trainer trainer = gymFacade.getTrainerProfile(username);
        return ResponseEntity.ok(trainerMapper.toResponse(trainer));
    }

    @Operation(summary = "Update trainer profile", description = "Specialization is read-only and cannot " +
            "be changed through this endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PutMapping
    public ResponseEntity<TrainerProfileResponse> updateProfile(@Valid @RequestBody UpdateTrainerRequest request) {
        log.info("Updating trainer profile for username {}", request.getUsername());

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainer updatedData = new Trainer();
        updatedData.setUser(user);

        Trainer updated = gymFacade.updateTrainerProfile(request.getUsername(), updatedData, request.getActive());

        return ResponseEntity.ok(trainerMapper.toResponse(updated));
    }

    @Operation(summary = "Get trainer's trainings list", description = "Returns the trainer's trainings, " +
            "optionally filtered by period and trainee name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @Parameter(description = "Trainer's username", required = true) @PathVariable String username,
            @Parameter(description = "Start of the period (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @Parameter(description = "End of the period (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @Parameter(description = "Filter by trainee username")
            @RequestParam(required = false) String traineeName) {
        log.info("Fetching trainings for trainer username {}", username);
        List<Training> trainings = gymFacade.getTrainerTrainings(username, periodFrom, periodTo, traineeName);
        return ResponseEntity.ok(trainings.stream().map(trainingMapper::toTrainerTrainingResponse).toList());
    }

    @Operation(summary = "Activate or deactivate a trainer", description = "Not idempotent - calling it with " +
            "the status the trainer already has results in an error.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "409", description = "Trainer already has the requested status")
    })
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> setActiveStatus(
            @Parameter(description = "Trainer's username", required = true) @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        log.info("Setting active status {} for trainer username {}", request.getActive(), username);
        gymFacade.setTrainerActiveStatus(username, request.getActive());
        return ResponseEntity.ok().build();
    }
}
