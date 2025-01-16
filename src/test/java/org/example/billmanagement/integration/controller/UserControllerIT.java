package org.example.billmanagement.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.UserDto;
import org.example.billmanagement.controller.exception.LoginAlreadyUsedException;
import org.example.billmanagement.model.Role;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.RoleRepository;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Add a default role for testing
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password123");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturnBadRequestWhenUserAlreadyExists() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setPassword("p".repeat(60));
        userRepository.save(existingUser);

        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password123");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Throwable exception = result.getResolvedException();
                    assert exception instanceof LoginAlreadyUsedException;
                });
    }

    @Test
    void shouldReturnValidationErrorWhenInputIsInvalid() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("");
        userDto.setPassword("short");
        userDto.setFirstName("T");
        userDto.setLastName("U");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
