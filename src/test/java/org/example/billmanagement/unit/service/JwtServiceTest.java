package org.example.billmanagement.unit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.example.billmanagement.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TOKEN_VALIDITY = 3600; // 1 hour in seconds

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService,"jwtKey",SECRET_KEY);
        ReflectionTestUtils.setField(jwtService,"tokenValidityInSeconds",TOKEN_VALIDITY);
    }

    @Test
    void testGenerateToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractExpiration() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUser() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        when(userDetails.getUsername()).thenReturn("anotherUser");

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        String username = "testUser";
        ReflectionTestUtils.setField(jwtService,"tokenValidityInSeconds",-3600); // Set token validity to the past

        String token = jwtService.generateToken(username);

        when(userDetails.getUsername()).thenReturn(username);

        assertThrows(ExpiredJwtException.class, () -> {
            ReflectionTestUtils.invokeMethod(jwtService,"isTokenExpired",token);
        });
    }

    @Test
    void testExtractClaim() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        String extractedUsername = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testIsTokenNotExpired() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        boolean isExpired = ReflectionTestUtils.invokeMethod(jwtService,"isTokenExpired",token);

        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_ExpiredToken() {
        String username = "testUser";
        ReflectionTestUtils.setField(jwtService,"tokenValidityInSeconds",-3600);

        String token = jwtService.generateToken(username);

        assertThrows(ExpiredJwtException.class, () -> {
            ReflectionTestUtils.invokeMethod(jwtService,"isTokenExpired",token);
        });
    }

}