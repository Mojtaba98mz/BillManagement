package org.example.billmanagement.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.AuthenticationController;
import org.example.billmanagement.controller.dto.LoginDto;
import org.example.billmanagement.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void authorize_shouldReturnToken_whenAuthenticationIsSuccessful() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken("testuser")).thenReturn("mockToken");

        ResponseEntity<?> response = authenticationController.authorize(loginDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        // Deserialize and verify the response body
        String responseBody = objectMapper.writeValueAsString(response.getBody());
        Map<String, String> bodyMap = objectMapper.readValue(responseBody, Map.class);
        assertEquals("mockToken", bodyMap.get("token"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken("testuser");
    }

    @Test
    void authorize_shouldThrowException_whenAuthenticationFails() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Invalid user request!"));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authenticationController.authorize(loginDto)
        );

        assertEquals("Invalid user request!", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(anyString());
    }
}
