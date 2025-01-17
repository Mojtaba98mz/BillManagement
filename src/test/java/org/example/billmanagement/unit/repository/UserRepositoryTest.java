package org.example.billmanagement.unit.repository;

import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindOneWithAuthoritiesByUsername_Found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(userRepository.findOneWithAuthoritiesByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findOneWithAuthoritiesByUsername("testUser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testUser");
        verify(userRepository, times(1)).findOneWithAuthoritiesByUsername("testUser");
    }

    @Test
    void testFindOneWithAuthoritiesByUsername_NotFound() {
        when(userRepository.findOneWithAuthoritiesByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findOneWithAuthoritiesByUsername("unknown");

        assertThat(result).isNotPresent();
        verify(userRepository, times(1)).findOneWithAuthoritiesByUsername("unknown");
    }

    @Test
    void shouldFindOneByUsername() {
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password123");

        when(userRepository.findOneByUsername("testUser"))
                .thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findOneByUsername("testUser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testUser");
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFoundInFindOneByUsername() {
        when(userRepository.findOneByUsername("nonExistingUser")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findOneByUsername("nonExistingUser");

        assertThat(result).isEmpty();
    }
}
