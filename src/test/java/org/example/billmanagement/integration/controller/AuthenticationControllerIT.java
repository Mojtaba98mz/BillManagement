package org.example.billmanagement.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.LoginDto;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthenticationControllerIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    void testAuthorize() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(passwordEncoder.encode("test"));

        userRepository.saveAndFlush(user);

        LoginDto login = new LoginDto();
        login.setUsername("testUser");
        login.setPassword("test");
        mockMvc
                .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testAuthorizeFails() throws Exception {
        LoginDto login = new LoginDto();
        login.setUsername("wrong-user");
        login.setPassword("wrong_password");
        mockMvc
                .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }
}
