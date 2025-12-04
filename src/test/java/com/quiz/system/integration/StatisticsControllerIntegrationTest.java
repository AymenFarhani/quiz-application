package com.quiz.system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.system.controller.StatisticsController;
import com.quiz.system.dto.ResponseDto;
import com.quiz.system.model.Answer;
import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import com.quiz.system.service.CustomUserDetailsService;
import com.quiz.system.service.StatisticsService;
import com.quiz.system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatisticsService statisticsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetQuestions() throws Exception {
        when(statisticsService.getQuestions()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/statistics/questions"))
                .andExpect(status().isOk());

        verify(statisticsService).getQuestions();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetStatistics() throws Exception {
        when(statisticsService.getStatistics()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/statistics"))
                .andExpect(status().isOk());

        verify(statisticsService).getStatistics();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testSubmitAnswers_Success() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        List<ResponseDto> responses = List.of(
                new ResponseDto(1, Answer.GO),
                new ResponseDto(2, Answer.NO_GO),
                new ResponseDto(3, Answer.GO),
                new ResponseDto(4, Answer.NO_GO),
                new ResponseDto(5, Answer.GO),
                new ResponseDto(6, Answer.NO_GO),
                new ResponseDto(7, Answer.GO),
                new ResponseDto(8, Answer.NO_GO),
                new ResponseDto(9, Answer.GO),
                new ResponseDto(10, Answer.NO_GO)
        );

        mockMvc.perform(post("/api/v1/statistics/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responses)))
                .andExpect(status().isOk())
                .andExpect(content().string("Answers submitted successfully!"));

        verify(statisticsService).submitAnswers(testUser, responses);
    }

    @Test
    @WithMockUser(username = "unknown@example.com")
    void testSubmitAnswers_UserNotFound() throws Exception {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        List<ResponseDto> responses = List.of();

        mockMvc.perform(post("/api/v1/statistics/submit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responses)))
                .andExpect(status().isBadRequest());
    }
}

