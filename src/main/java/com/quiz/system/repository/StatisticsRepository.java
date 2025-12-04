package com.quiz.system.repository;

import com.quiz.system.dto.QuestionDto;
import com.quiz.system.model.Statistics;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatisticsRepository {
    private final JdbcTemplate jdbcTemplate;

    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String findByQuestionNumber(Integer questionNumber) {
        String GET_QUESTION_SQL = "SELECT text FROM questions WHERE id = ?";
        return jdbcTemplate.queryForObject(GET_QUESTION_SQL, String.class, questionNumber);
    }

    public Statistics findStatisticsByQuestionNumber(Integer questionNumber) {
        String GET_STATISTICS_SQL = "SELECT * FROM statistics WHERE question_number = ?";
        List<Statistics> result = jdbcTemplate.query(GET_STATISTICS_SQL,new BeanPropertyRowMapper<>(Statistics.class), questionNumber);

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public List<Statistics> findAll() {
        String GET_USER_SQL = "SELECT * FROM statistics";
        return jdbcTemplate.query(GET_USER_SQL, (rs, rowNum) -> {
            Statistics statistic = new Statistics();
            statistic.setId(rs.getLong("id"));
            statistic.setQuestionNumber(rs.getInt("question_number"));
            statistic.setGoCount(rs.getLong("go_count"));
            statistic.setNoGoCount(rs.getLong("no_go_count"));
            return statistic;
        });
    }

    public List<QuestionDto> getQuestions() {
        String GET_ALL_QUESTIONS = "SELECT * FROM questions";
        return jdbcTemplate.query(GET_ALL_QUESTIONS, (rs, rowNum) -> new QuestionDto(
                rs.getInt("id"),
                rs.getString("text")
        ));
    }

    public void updateStatistics(Statistics statistics) {
        String UPDATE_STATISTICS_SQL = "UPDATE statistics SET go_count = ?, no_go_count = ? WHERE question_number = ?";
        jdbcTemplate.update(UPDATE_STATISTICS_SQL, statistics.getGoCount(), statistics.getNoGoCount(), statistics.getQuestionNumber());
    }

    public void createStatistics(Statistics statistics) {
        String INSERT_STATISTICS = "INSERT INTO statistics (question_number, go_count, no_go_count) VALUES (?, ?, ?)";
        jdbcTemplate.update(INSERT_STATISTICS, statistics.getQuestionNumber(), statistics.getGoCount(), statistics.getNoGoCount());
    }
}
