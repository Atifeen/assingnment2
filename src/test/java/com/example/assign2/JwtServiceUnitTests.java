package com.example.assign2;

import com.example.assign2.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceUnitTests {

    @Test
    void generateAndValidateToken() throws Exception {
        String secret = "01234567890123456789012345678901"; // 32 chars
        JwtService jwtService = new JwtService(secret, 3600);

        UserDetails user = User.withUsername("alice").password("pw").roles("STUDENT").build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("alice", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }
}
