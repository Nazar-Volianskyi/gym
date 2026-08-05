package com.epam.gym.controller;

import com.epam.gym.dao.TrainingTypeDao;
import com.epam.gym.exception.EntityNotFoundException;
import com.epam.gym.facade.GymFacade;
import com.epam.gym.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @MockitoBean
    private TrainingTypeDao trainingTypeDao;

    @Test
    void addTraining_shouldReturn200_andDelegateToFacade() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "Nazar.Volianskyi",
                                    "trainerUsername": "Nazar.Volianskyi",
                                    "trainingName": "Morning Cardio",
                                    "trainingDate": "2026-01-15",
                                    "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isOk());

        verify(gymFacade, times(1)).addTraining(
                "Nazar.Volianskyi", "Nazar.Volianskyi", "Morning Cardio", LocalDate.of(2026, 1, 15), 60);
    }

    @Test
    void addTraining_shouldReturn404_whenTraineeOrTrainerNotFound() throws Exception {
       doThrow(new EntityNotFoundException("Trainee", "Nobody"))
                .when(gymFacade).addTraining("Nobody", "Nazar.Volianskyi", "Cardio", LocalDate.of(2026, 1, 15), 60);

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "Nobody",
                                    "trainerUsername": "Nazar.Volianskyi",
                                    "trainingName": "Cardio",
                                    "trainingDate": "2026-01-15",
                                    "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTraining_shouldReturn400_whenDurationNotPositive() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "Nazar.Volianskyi",
                                    "trainerUsername": "Nazar.Volianskyi",
                                    "trainingName": "Cardio",
                                    "trainingDate": "2026-01-15",
                                    "trainingDuration": 0
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrainingTypes_shouldReturn200_withMappedList() throws Exception {
        TrainingType cardio = new TrainingType();
        cardio.setId(1L);
        cardio.setTrainingTypeName("Cardio");
        when(trainingTypeDao.findAll()).thenReturn(List.of(cardio));

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Cardio"));
    }
}
