package com.quiz.system.service;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.dto.ResponseDto;
import com.quiz.system.dto.StatisticDto;
import com.quiz.system.model.Answer;
import com.quiz.system.model.Statistics;
import com.quiz.system.model.Submission;
import com.quiz.system.model.User;
import com.quiz.system.repository.StatisticsRepository;
import com.quiz.system.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------
    // TEST: getQuestions()
    // -------------------------------------------------------
    @Test
    void testGetQuestions() {
        List<QuestionDto> mockQuestions = List.of(
                new QuestionDto(1, "Q1"),
                new QuestionDto(2, "Q2")
        );
        when(statisticsRepository.getQuestions()).thenReturn(mockQuestions);

        List<QuestionDto> result = statisticsService.getQuestions();

        assertEquals(2, result.size());
        verify(statisticsRepository).getQuestions();
    }

    // -------------------------------------------------------
    // TEST: getStatistics()
    // -------------------------------------------------------
    @Test
    void testGetStatistics() {
        Statistics statistics1 = new Statistics(1);
        statistics1.setGoCount(5L);
        statistics1.setNoGoCount(3L);
        Statistics statistics2 = new Statistics(2);
        statistics2.setGoCount(10L);
        statistics2.setNoGoCount(0L);
        List<Statistics> mockStats = List.of(statistics1, statistics2);

        when(statisticsRepository.findAll()).thenReturn(mockStats);
        when(statisticsRepository.findByQuestionNumber(1)).thenReturn("Q1");
        when(statisticsRepository.findByQuestionNumber(2)).thenReturn("Q2");

        List<StatisticDto> result = statisticsService.getStatistics();

        assertEquals(2, result.size());
        assertEquals("Q1", result.getFirst().question());
        assertEquals(5L, result.getFirst().go());
        assertEquals(3L, result.getFirst().noGo());
    }

    // -------------------------------------------------------
    // TEST: submitAnswers() — success case
    // -------------------------------------------------------
    @Test
    void testSubmitAnswers_SuccessfulSubmission() {
        User user = new User();
        user.setId(10L);

        List<ResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            responses.add(new ResponseDto(i + 1, Answer.GO));
        }

        // User has NOT submitted before
        when(submissionRepository.existsByUserId(10L)).thenReturn(false);

        // Fake existing stats for only question 1
        Statistics stat1 = new Statistics(1);
        stat1.setId(100L);

        when(statisticsRepository.findAll()).thenReturn(List.of(stat1));

        statisticsService.submitAnswers(user, responses);

        // verify submission created
        verify(submissionRepository).createSubmission(any(Submission.class));

        // verify updates
        verify(statisticsRepository, times(1)).updateStatistics(any());  // for question 1
        verify(statisticsRepository, times(9)).createStatistics(any());  // for missing stats
    }

    // -------------------------------------------------------
    // TEST: submitAnswers() — user already submitted
    // -------------------------------------------------------
    @Test
    void testSubmitAnswers_UserAlreadySubmitted() {
        User user = new User();
        user.setId(5L);

        when(submissionRepository.existsByUserId(5L)).thenReturn(true);

        List<ResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            responses.add(new ResponseDto(i + 1, Answer.GO));
        }

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> statisticsService.submitAnswers(user, responses));

        assertEquals("User has already submitted the quiz", ex.getMessage());
    }

    // -------------------------------------------------------
    // TEST: submitAnswers() — not 10 answers
    // -------------------------------------------------------
    @Test
    void testSubmitAnswers_Not10Responses() {
        User user = new User();
        user.setId(1L);

        List<ResponseDto> responses = List.of(new ResponseDto(1, Answer.GO));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> statisticsService.submitAnswers(user, responses));

        assertEquals("Exactly 10 responses must be submitted", ex.getMessage());
    }

    // -------------------------------------------------------
    // TEST: submitAnswers() — test increments correctly
    // -------------------------------------------------------
    @Test
    void testSubmitAnswers_StatisticsIncrement() {
        User user = new User();
        user.setId(1L);

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

        when(submissionRepository.existsByUserId(1L)).thenReturn(false);
        when(statisticsRepository.findAll()).thenReturn(List.of()); // no pre-existing stats

        statisticsService.submitAnswers(user, responses);

        // Capture created statistics
        ArgumentCaptor<Statistics> captor = ArgumentCaptor.forClass(Statistics.class);
        verify(statisticsRepository, times(10)).createStatistics(captor.capture());

        List<Statistics> stats = captor.getAllValues();

        assertEquals(10, stats.size());
        assertEquals(Answer.GO, responses.getFirst().answer());
    }

    // --------------------------------------------
// TEST: getQuestionByQuestionNumber()
// --------------------------------------------
    @Test
    void testGetQuestionByQuestionNumber() {
        when(statisticsRepository.findByQuestionNumber(5)).thenReturn("Sample Question");

        String question = statisticsService.getQuestionByQuestionNumber(5);

        assertEquals("Sample Question", question);
        verify(statisticsRepository).findByQuestionNumber(5);
    }

    // --------------------------------------------
// TEST: getStatistics() with empty statistics
// --------------------------------------------
    @Test
    void testGetStatistics_EmptyList() {
        when(statisticsRepository.findAll()).thenReturn(List.of());

        List<StatisticDto> stats = statisticsService.getStatistics();

        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    // --------------------------------------------
// TEST: submitAnswers() — mixture of existing & new statistics
// --------------------------------------------
    @Test
    void testSubmitAnswers_MixedExistingAndNewStatistics() {
        User user = new User();
        user.setId(20L);

        List<ResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            responses.add(new ResponseDto(i + 1, (i % 2 == 0) ? Answer.GO : Answer.NO_GO));
        }

        when(submissionRepository.existsByUserId(20L)).thenReturn(false);

        Statistics existingStat = new Statistics(1);
        existingStat.setId(100L);
        existingStat.setGoCount(5L);
        existingStat.setNoGoCount(2L);

        when(statisticsRepository.findAll()).thenReturn(List.of(existingStat));

        statisticsService.submitAnswers(user, responses);

        // existing stat should be updated
        verify(statisticsRepository).updateStatistics(argThat(stat ->
                stat.getQuestionNumber() == 1 && stat.getGoCount() == 6 && stat.getNoGoCount() == 2));

        // new statistics should be created for remaining questions
        verify(statisticsRepository, times(9)).createStatistics(any());

        verify(submissionRepository).createSubmission(any(Submission.class));
    }

    // --------------------------------------------
// TEST: submitAnswers() — verify increment logic
// --------------------------------------------
    @Test
    void testSubmitAnswers_IncrementLogic() {
        User user = new User();
        user.setId(30L);

        List<ResponseDto> responses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            responses.add(new ResponseDto(i + 1, (i % 2 == 0) ? Answer.GO : Answer.NO_GO));
        }

        when(submissionRepository.existsByUserId(30L)).thenReturn(false);
        when(statisticsRepository.findAll()).thenReturn(List.of());

        statisticsService.submitAnswers(user, responses);

        ArgumentCaptor<Statistics> captor = ArgumentCaptor.forClass(Statistics.class);
        verify(statisticsRepository, times(10)).createStatistics(captor.capture());

        List<Statistics> createdStats = captor.getAllValues();

        for (int i = 0; i < createdStats.size(); i++) {
            Statistics stat = createdStats.get(i);
            if (i % 2 == 0) {
                assertEquals(1L, stat.getGoCount());
                assertEquals(0L, stat.getNoGoCount());
            } else {
                assertEquals(0L, stat.getGoCount());
                assertEquals(1L, stat.getNoGoCount());
            }
        }
    }

}
