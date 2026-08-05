package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TraineeProfileResponse;
import com.epam.gym.dto.response.TrainerInfoResponse;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeMapperTest {

    private final TraineeMapper mapper = new TraineeMapper();

    private Trainer buildTrainer(String username, String specializationName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(specializationName);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }

    @Test
    void toResponse_shouldMapAllFieldsIncludingTrainers() {
        User user = new User();
        user.setUsername("Mark.Dok");
        user.setFirstName("Mark");
        user.setLastName("Dok");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(2000, 5, 20));
        trainee.setAddress("Lviv");
        trainee.setTrainers(List.of(buildTrainer("Nazar.Volianskyi", "Cardio")));

        TraineeProfileResponse response = mapper.toResponse(trainee);

        assertEquals("Mark.Dok", response.getUsername());
        assertEquals("Mark", response.getFirstName());
        assertEquals("Dok", response.getLastName());
        assertEquals(LocalDate.of(2000, 5, 20), response.getDateOfBirth());
        assertEquals("Lviv", response.getAddress());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainers().size());
        assertEquals("Nazar.Volianskyi", response.getTrainers().get(0).getUsername());
        assertEquals("Cardio", response.getTrainers().get(0).getSpecialization());
    }

    @Test
    void mapTrainers_shouldMapEachTrainerToInfoResponse() {
        List<Trainer> trainers = List.of(
                buildTrainer("Nazar1.Volianskyi", "Cardio"),
                buildTrainer("Nazar2.Volianskyi", "Yoga"));

        List<TrainerInfoResponse> result = mapper.mapTrainers(trainers);

        assertEquals(2, result.size());
        assertEquals("Cardio", result.get(0).getSpecialization());
        assertEquals("Yoga", result.get(1).getSpecialization());
    }
}
