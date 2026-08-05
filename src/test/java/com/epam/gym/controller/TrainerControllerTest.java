package com.epam.gym.controller;

import com.epam.gym.dto.mapper.TrainerMapper;
import com.epam.gym.dto.mapper.TrainingMapper;
import com.epam.gym.dto.response.TrainerProfileResponse;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.Trainer;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private TrainerMapper trainerMapper;

    @MockitoBean
    private TrainingMapper trainingMapper;

    private Trainer buildTrainer(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        user.setPassword("generatedPass");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    @Test
    void register_shouldReturn201_withUsernameAndPassword() throws Exception {
        when(gymFacade.createTrainer("Nazar", "Volianskyi", "Cardio")).thenReturn(buildTrainer("Nazar.Volianskyi"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Nazar",
                                    "lastName": "Volianskyi",
                                    "specialization": "Cardio"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Nazar.Volianskyi"));
    }

    @Test
    void register_shouldReturn404_whenSpecializationNotFound() throws Exception {
        when(gymFacade.createTrainer("Nazar", "Volianskyi", "Unknown"))
                .thenThrow(new EntityNotFoundException("TrainingType", "Unknown"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Nazar",
                                    "lastName": "Volianskyi",
                                    "specialization": "Unknown"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_shouldReturn409_whenAlreadyRegisteredAsTrainee() throws Exception {
        when(gymFacade.createTrainer("Nazar", "Volianskyi", "Cardio"))
                .thenThrow(new ConflictException("Nazar Volianskyi is already registered as a trainee"));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Nazar",
                                    "lastName": "Volianskyi",
                                    "specialization": "Cardio"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void getProfile_shouldReturn200() throws Exception {
        Trainer trainer = buildTrainer("Nazar.Volianskyi");
        when(gymFacade.getTrainerProfile("Nazar.Volianskyi")).thenReturn(trainer);
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setUsername("Nazar.Volianskyi");
        when(trainerMapper.toResponse(trainer)).thenReturn(response);

        mockMvc.perform(get("/api/trainers/Nazar.Volianskyi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Nazar.Volianskyi"));
    }

    @Test
    void getProfile_shouldReturn404_whenNotFound() throws Exception {
        when(gymFacade.getTrainerProfile("nothing")).thenThrow(new EntityNotFoundException("Trainer", "nothing"));

        mockMvc.perform(get("/api/trainers/nothing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setActiveStatus_shouldReturn200_andDelegateToFacade() throws Exception {
        mockMvc.perform(patch("/api/trainers/Nazar.Volianskyi/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "active": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(gymFacade, times(1)).setTrainerActiveStatus("Nazar.Volianskyi", false);
    }

    @Test
    void getTrainings_shouldReturn200_withEmptyList() throws Exception {
        when(gymFacade.getTrainerTrainings("Nazar.Volianskyi", null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/trainers/Nazar.Volianskyi/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
