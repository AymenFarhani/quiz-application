package com.quiz.system.repository;

import com.quiz.system.model.Submission;
import com.quiz.system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private SubmissionRepository submissionRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        submissionRepository = new SubmissionRepository(jdbcTemplate);
    }

    @Test
    void testExistsByUserId_WhenUserExists() {
        Long userId = 1L;
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(userId))).thenReturn(3);

        boolean exists = submissionRepository.existsByUserId(userId);

        assertTrue(exists);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(userId));
    }

    @Test
    void testExistsByUserId_WhenUserDoesNotExist() {
        Long userId = 2L;
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(userId))).thenReturn(0);

        boolean exists = submissionRepository.existsByUserId(userId);

        assertFalse(exists);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(userId));
    }

    @Test
    void testCreateSubmission() {
        User user = new User();
        user.setId(1L);
        Submission submission = new Submission(user);

        // Capture arguments passed to jdbcTemplate.update
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        submissionRepository.createSubmission(submission);

        verify(jdbcTemplate).update(anyString(), userIdCaptor.capture(), dateCaptor.capture());

        assertEquals(1L, userIdCaptor.getValue());
        assertNotNull(dateCaptor.getValue());
        // dateCaptor.getValue() is the current LocalDateTime, we just ensure it's not null
    }
}
