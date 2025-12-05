package com.quiz.system.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "your256bitsecret-your256bitsecret-your256bitsecret";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSECRET(secret);// directly inject secret
    }

    @Test
    void testGenerateTokenNotNull() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");
    }

    @Test
    void testExtractEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        String extractedEmail = jwtUtil.extractEmail(token);
        assertEquals(email, extractedEmail, "Extracted email should match the original email");
    }

    @Test
    void testTokenExpiration() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        long now = System.currentTimeMillis();
        long exp = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();

        assertTrue(exp > now, "Token expiration should be in the future");
    }
}
