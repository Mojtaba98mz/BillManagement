package org.example.billmanagement.unit.repository;

import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOneWithAuthoritiesByUsername_Found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findOneWithAuthoritiesByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findOneWithAuthoritiesByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findOneWithAuthoritiesByUsername("testuser");
    }

    @Test
    void testFindOneWithAuthoritiesByUsername_NotFound() {
        when(userRepository.findOneWithAuthoritiesByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findOneWithAuthoritiesByUsername("unknown");

        assertThat(result).isNotPresent();
        verify(userRepository, times(1)).findOneWithAuthoritiesByUsername("unknown");
    }
}
