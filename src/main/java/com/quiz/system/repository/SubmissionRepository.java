package com.quiz.system.repository;

import com.quiz.system.model.Submission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SubmissionRepository{

    private final JdbcTemplate jdbcTemplate;

    public SubmissionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM submissions WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    public void createSubmission(Submission submission) {
        String CREATE_NEW_SUBMISSION = "INSERT INTO submissions (user_id, submitted_at) VALUES(?,?)";
        jdbcTemplate.update(CREATE_NEW_SUBMISSION, submission.getUser().getId(), LocalDateTime.now());
    }
}
