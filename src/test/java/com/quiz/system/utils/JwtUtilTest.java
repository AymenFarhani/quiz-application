package com.quiz.system.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "key.secret=your256bitsecret-your256bitsecret-your256bitsecret")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

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
                .setSigningKey(Keys.hmacShaKeyFor("your256bitsecret-your256bitsecret-your256bitsecret".getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();

        assertTrue(exp > now, "Token expiration should be in the future");
    }
}
