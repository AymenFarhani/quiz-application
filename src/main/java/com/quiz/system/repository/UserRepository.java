package com.quiz.system.repository;

import com.quiz.system.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByEmail(String email){
        String GET_USER_SQL = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(GET_USER_SQL, new BeanPropertyRowMapper<>(User.class), email);
    }
}
