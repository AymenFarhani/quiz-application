package com.quiz.system.repository;

import com.quiz.system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        userRepository = new UserRepository(jdbcTemplate);
    }

    @Test
    void testFindByEmail_UserExists() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);

        when(jdbcTemplate.queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq(email)))
                .thenReturn(mockUser);

        User user = userRepository.findByEmail(email);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(1L, user.getId());
        verify(jdbcTemplate).queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq(email));
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        String email = "nonexistent@example.com";

        when(jdbcTemplate.queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq(email)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class,
                () -> userRepository.findByEmail(email));

        verify(jdbcTemplate).queryForObject(anyString(), any(BeanPropertyRowMapper.class), eq(email));
    }
}
