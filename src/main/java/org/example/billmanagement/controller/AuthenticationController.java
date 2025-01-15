package org.example.billmanagement.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.example.billmanagement.controller.dto.LoginDto;
import org.example.billmanagement.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return new ResponseEntity<>(new JWTToken(jwtService.generateToken(loginDto.getUsername())), HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    private static class JWTToken {

        private String token;

        JWTToken(String token) {
            this.token = token;
        }

        @JsonProperty("token")
        String getIdToken() {
            return token;
        }

        void setIdToken(String token) {
            this.token = token;
        }
    }
}
