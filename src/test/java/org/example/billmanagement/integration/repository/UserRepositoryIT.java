package org.example.billmanagement.integration.repository;

import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        entityManager.clear();

        // Create a test user
        user = new User();
        user.setUsername("testUser");
        user.setPassword("p".repeat(60));
        user.setAuthorities(Collections.emptySet());

        // Persist the user in the database
        entityManager.persistAndFlush(user);
    }

    @Test
    void findOneWithAuthoritiesByUsername_ExistingUsername_ReturnsUser() {
        Optional<User> foundUser = userRepository.findOneWithAuthoritiesByUsername(user.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void findOneWithAuthoritiesByUsername_NonExistingUsername_ReturnsEmpty() {
        Optional<User> foundUser = userRepository.findOneWithAuthoritiesByUsername("nonExistingUser");
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testPasswordInvalidLengthValidation() {

        User invalidUser = new User();
        invalidUser.setUsername("testUser");
        invalidUser.setPassword("p".repeat(60));
        invalidUser.setAuthorities(Collections.emptySet());

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(invalidUser);
        });
    }
}