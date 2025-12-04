package com.quiz.system.controller;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.dto.ResponseDto;
import com.quiz.system.dto.StatisticDto;
import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import com.quiz.system.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsControllerTest {

    private StatisticsService statisticsService;
    private UserRepository userRepository;
    private StatisticsController controller;

    @BeforeEach
    void setUp() {
        statisticsService = mock(StatisticsService.class);
        userRepository = mock(UserRepository.class);
        controller = new StatisticsController(statisticsService, userRepository);
    }

    @Test
    void testGetQuestions() {
        List<QuestionDto> questions = List.of(new QuestionDto(1, "Q1"));
        when(statisticsService.getQuestions()).thenReturn(questions);

        ResponseEntity<List<QuestionDto>> response = controller.getQuestions();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Q1", response.getBody().getFirst().text());
    }

    @Test
    void testGetStatistics() {
        List<StatisticDto> stats = List.of(new StatisticDto("Q1", 2L, 1L));
        when(statisticsService.getStatistics()).thenReturn(stats);

        ResponseEntity<List<StatisticDto>> response = controller.getStatistics();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Q1", response.getBody().getFirst().question());
        assertEquals(2L, response.getBody().getFirst().go());
    }

    @Test
    void testSubmitAnswers_UserExists() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        List<ResponseDto> responses = List.of(
                new ResponseDto(1, com.quiz.system.model.Answer.GO),
                new ResponseDto(2, com.quiz.system.model.Answer.NO_GO),
                new ResponseDto(3, com.quiz.system.model.Answer.GO),
                new ResponseDto(4, com.quiz.system.model.Answer.NO_GO),
                new ResponseDto(5, com.quiz.system.model.Answer.GO),
                new ResponseDto(6, com.quiz.system.model.Answer.NO_GO),
                new ResponseDto(7, com.quiz.system.model.Answer.GO),
                new ResponseDto(8, com.quiz.system.model.Answer.NO_GO),
                new ResponseDto(9, com.quiz.system.model.Answer.GO),
                new ResponseDto(10, com.quiz.system.model.Answer.NO_GO)
        );

        ResponseEntity<String> response = controller.submitAnswers(userDetails, responses);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Answers submitted successfully!", response.getBody());

        // Verify that the service was called
        verify(statisticsService).submitAnswers(user, responses);
    }

    @Test
    void testSubmitAnswers_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        List<ResponseDto> responses = List.of(); // empty list for simplicity

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> controller.submitAnswers(userDetails, responses));
    }
}
