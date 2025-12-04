package com.quiz.system.service;

import com.quiz.system.model.User;
import com.quiz.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("secret");
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}
