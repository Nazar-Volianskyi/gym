package com.epam.gym.dto.mapper;

import com.epam.gym.dto.response.TrainerProfileResponse;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.TrainingType;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    private final TrainerMapper mapper = new TrainerMapper();

    @Test
    void toResponse_shouldMapAllFieldsIncludingTrainees() {
        User trainerUser = new User();
        trainerUser.setUsername("Nazar.Volianskyi");
        trainerUser.setFirstName("Nazar");
        trainerUser.setLastName("Volianskyi");
        trainerUser.setActive(true);

        TrainingType cardio = new TrainingType();
        cardio.setTrainingTypeName("Cardio");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(cardio);

        User traineeUser = new User();
        traineeUser.setUsername("Nazar1.Volianskyi");
        traineeUser.setFirstName("Nazar1");
        traineeUser.setLastName("Volianskyi");
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainer.setTrainees(List.of(trainee));

        TrainerProfileResponse response = mapper.toResponse(trainer);

        assertEquals("Nazar.Volianskyi", response.getUsername());
        assertEquals("Nazar", response.getFirstName());
        assertEquals("Volianskyi", response.getLastName());
        assertEquals("Cardio", response.getSpecialization());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainees().size());
        assertEquals("Nazar1.Volianskyi", response.getTrainees().get(0).getUsername());
        assertEquals("Nazar1", response.getTrainees().get(0).getFirstName());
        assertEquals("Volianskyi", response.getTrainees().get(0).getLastName());
    }
}
