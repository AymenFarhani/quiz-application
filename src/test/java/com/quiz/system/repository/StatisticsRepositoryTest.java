package com.quiz.system.repository;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.model.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatisticsRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private StatisticsRepository statisticsRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        statisticsRepository = new StatisticsRepository(jdbcTemplate);
    }

    @Test
    void testFindByQuestionNumber() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(1))).thenReturn("Sample Question");

        String question = statisticsRepository.findByQuestionNumber(1);

        assertEquals("Sample Question", question);
        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class), eq(1));
    }

    @Test
    void testFindStatisticsByQuestionNumber_Found() {
        Statistics stat = new Statistics();
        stat.setId(1L);
        stat.setQuestionNumber(2);
        stat.setGoCount(5L);
        stat.setNoGoCount(3L);

        when(jdbcTemplate.query(anyString(), any(BeanPropertyRowMapper.class), eq(2)))
                .thenReturn(List.of(stat));

        Statistics result = statisticsRepository.findStatisticsByQuestionNumber(2);

        assertNotNull(result);
        assertEquals(2, result.getQuestionNumber());
        assertEquals(5L, result.getGoCount());
        assertEquals(3L, result.getNoGoCount());
    }

    @Test
    void testFindStatisticsByQuestionNumber_NotFound() {
        when(jdbcTemplate.query(anyString(), any(BeanPropertyRowMapper.class), eq(3)))
                .thenReturn(List.of());

        Statistics result = statisticsRepository.findStatisticsByQuestionNumber(3);

        assertNull(result);
    }

    @Test
    void testFindAll() {
        // Mock the JdbcTemplate.query(String sql, RowMapper<T> rowMapper) method
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(
                        new Statistics() {{
                            setId(1L);
                            setQuestionNumber(1);
                            setGoCount(2L);
                            setNoGoCount(3L);
                        }},
                        new Statistics() {{
                            setId(2L);
                            setQuestionNumber(2);
                            setGoCount(5L);
                            setNoGoCount(1L);
                        }}
                ));

        List<Statistics> stats = statisticsRepository.findAll();

        assertEquals(2, stats.size());
        assertEquals(1, stats.getFirst().getQuestionNumber());
        assertEquals(2L, stats.get(0).getGoCount());
        assertEquals(3L, stats.get(0).getNoGoCount());
        assertEquals(2, stats.get(1).getQuestionNumber());
    }

    @Test
    void testGetQuestions() {
        // Mock the JdbcTemplate.query(String sql, RowMapper<T> rowMapper) method
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(
                        new QuestionDto(1, "Q1"),
                        new QuestionDto(2, "Q2")
                ));

        List<QuestionDto> questions = statisticsRepository.getQuestions();

        assertEquals(2, questions.size());
        assertEquals("Q1", questions.get(0).text());
        assertEquals("Q2", questions.get(1).text());
    }


    @Test
    void testUpdateStatistics() {
        Statistics stat = new Statistics();
        stat.setQuestionNumber(5);
        stat.setGoCount(10L);
        stat.setNoGoCount(20L);

        statisticsRepository.updateStatistics(stat);

        verify(jdbcTemplate).update(anyString(), eq(10L), eq(20L), eq(5));
    }

    @Test
    void testCreateStatistics() {
        Statistics stat = new Statistics();
        stat.setQuestionNumber(7);
        stat.setGoCount(4L);
        stat.setNoGoCount(6L);

        statisticsRepository.createStatistics(stat);

        verify(jdbcTemplate).update(anyString(), eq(7), eq(4L), eq(6L));
    }

    @Test
    void testFindByQuestionNumber_NotFound() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(10))).thenReturn(null);

        String question = statisticsRepository.findByQuestionNumber(10);

        assertNull(question);
        verify(jdbcTemplate).queryForObject(anyString(), eq(String.class), eq(10));
    }

    @Test
    void testFindByQuestionNumber_Exception() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(1)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> statisticsRepository.findByQuestionNumber(1));

        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void testFindAll_Empty() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(List.of());

        List<Statistics> stats = statisticsRepository.findAll();

        assertNotNull(stats);
        assertTrue(stats.isEmpty());
    }

    @Test
    void testGetQuestions_Empty() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(List.of());

        List<QuestionDto> questions = statisticsRepository.getQuestions();

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void testUpdateStatistics_Exception() {
        Statistics stat = new Statistics();
        stat.setQuestionNumber(1);
        stat.setGoCount(1L);
        stat.setNoGoCount(2L);

        doThrow(new RuntimeException("DB error")).when(jdbcTemplate).update(anyString(), any(), any(), any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> statisticsRepository.updateStatistics(stat));

        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void testCreateStatistics_Exception() {
        Statistics stat = new Statistics();
        stat.setQuestionNumber(1);
        stat.setGoCount(1L);
        stat.setNoGoCount(2L);

        doThrow(new RuntimeException("DB error")).when(jdbcTemplate).update(anyString(), any(), any(), any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> statisticsRepository.createStatistics(stat));

        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void testFindStatisticsByQuestionNumber_Exception() {
        when(jdbcTemplate.query(anyString(), any(BeanPropertyRowMapper.class), eq(2)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> statisticsRepository.findStatisticsByQuestionNumber(2));

        assertEquals("DB error", ex.getMessage());
    }


}
