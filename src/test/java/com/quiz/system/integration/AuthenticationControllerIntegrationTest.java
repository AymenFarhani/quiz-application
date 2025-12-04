package com.quiz.system.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.system.controller.AuthenticationController;
import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import com.quiz.system.service.CustomUserDetailsService;
import com.quiz.system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private User userRequest;
    private User userFromDb;

    @BeforeEach
    void setUp() {
        userRequest = new User();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password");

        userFromDb = new User();
        userFromDb.setEmail("test@example.com");
        userFromDb.setPassword("hashedPassword");
    }

    @Test
    void testLogin_Success() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(userFromDb);
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password", "hashedPassword");
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void testLogin_InvalidPassword() throws Exception {
        when(userRepository.findByEmail("test@example.com")).thenReturn(userFromDb);
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(RuntimeException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Invalid email or password", result.getResolvedException().getMessage()));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password", "hashedPassword");
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
