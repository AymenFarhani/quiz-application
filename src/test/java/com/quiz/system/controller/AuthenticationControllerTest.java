package com.quiz.system.controller;
import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import com.quiz.system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        User request = new User();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User userFromDb = new User();
        userFromDb.setEmail("test@example.com");
        userFromDb.setPassword("hashedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(userFromDb);
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        // Act
        String token = authenticationController.login(request);

        // Assert
        assertEquals("jwt-token", token);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password", "hashedPassword");
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        User request = new User();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        User userFromDb = new User();
        userFromDb.setEmail("test@example.com");
        userFromDb.setPassword("hashedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(userFromDb);
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashedPassword");
        verify(jwtUtil, never()).generateToken(anyString());
    }
}

