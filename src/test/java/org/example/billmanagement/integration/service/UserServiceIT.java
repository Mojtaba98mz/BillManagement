package org.example.billmanagement.integration.service;

import org.example.billmanagement.model.Role;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.RoleRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserServiceIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder, roleRepository);
        Role defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        Optional<User> foundUser = userService.findOneByUsername("testuser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }
}


