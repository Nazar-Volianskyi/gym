package com.epam.gym.controller;

import com.epam.gym.dto.mapper.TraineeMapper;
import com.epam.gym.dto.mapper.TrainingMapper;
import com.epam.gym.dto.response.TraineeProfileResponse;
import com.epam.gym.exception.ConflictException;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.Trainee;
import com.epam.gym.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private TraineeMapper traineeMapper;

    @MockitoBean
    private TrainingMapper trainingMapper;

    private Trainee buildTrainee(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Nazar");
        user.setLastName("Volianskyi");
        user.setPassword("generatedPass");
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    void register_shouldReturn201_withUsernameAndPassword() throws Exception {
        when(gymFacade.createTrainee(org.mockito.ArgumentMatchers.any())).thenReturn(buildTrainee("Nazar.Volianskyi"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Nazar",
                                    "lastName": "Volianskyi"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Nazar.Volianskyi"))
                .andExpect(jsonPath("$.password").value("generatedPass"));
    }

    @Test
    void register_shouldReturn400_whenFirstNameMissing() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "lastName": "Volianskyi"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn409_whenAlreadyRegisteredAsTrainer() throws Exception {
        when(gymFacade.createTrainee(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new ConflictException("Nazar Volianskyi is already registered as a trainer"));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Nazar",
                                    "lastName": "Volianskyi"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void getProfile_shouldReturn200_withProfile() throws Exception {
        Trainee trainee = buildTrainee("Nazar.Volianskyi");
        when(gymFacade.getTraineeProfile("Nazar.Volianskyi")).thenReturn(trainee);
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setUsername("Nazar.Volianskyi");
        when(traineeMapper.toResponse(trainee)).thenReturn(response);

        mockMvc.perform(get("/api/trainees/Nazar.Volianskyi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Nazar.Volianskyi"));
    }

    @Test
    void getProfile_shouldReturn404_whenNotFound() throws Exception {
        when(gymFacade.getTraineeProfile("Nobody"))
                .thenThrow(new EntityNotFoundException("Trainee", "Nobody"));

        mockMvc.perform(get("/api/trainees/Nobody"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProfile_shouldReturn200_andDelegateToFacade() throws Exception {
        mockMvc.perform(delete("/api/trainees/Nazar.Volianskyi"))
                .andExpect(status().isOk());
    }

    @Test
    void getUnassignedTrainers_shouldReturn200() throws Exception {
        when(gymFacade.getUnassignedTrainers("Nazar.Volianskyi")).thenReturn(List.of());
        when(traineeMapper.mapTrainers(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/Nazar.Volianskyi/unassigned-trainers"))
                .andExpect(status().isOk());
    }

    @Test
    void setActiveStatus_shouldReturn200_whenChanged() throws Exception {
        mockMvc.perform(patch("/api/trainees/Nazar.Volianskyi/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "active": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(gymFacade, times(1)).setTraineeActiveStatus("Nazar.Volianskyi", false);
    }

    @Test
    void setActiveStatus_shouldReturn409_whenAlreadyInThatState() throws Exception {
        org.mockito.Mockito.doThrow(new ConflictException("Trainee Nazar.Volianskyi is already active"))
                .when(gymFacade).setTraineeActiveStatus("Nazar.Volianskyi", true);

        mockMvc.perform(patch("/api/trainees/Nazar.Volianskyi/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "active": true
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void getTrainings_shouldReturn200_withEmptyList() throws Exception {
        when(gymFacade.getTraineeTrainings("Nazar.Volianskyi", null, null, null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/trainees/Nazar.Volianskyi/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
