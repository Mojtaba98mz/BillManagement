package org.example.billmanagement.unit.service;

import org.example.billmanagement.controller.dto.UserDto;
import org.example.billmanagement.model.Role;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.RoleRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("password");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        Role role = new Role();
        role.setName("ROLE_USER");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User createdUser = userService.createUser(userDto);

        // Assert
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(createdUser.getAuthorities()).containsExactly(role);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldReturnEmptyIfUsernameNotFound() {
        when(userRepository.findOneByUsername("nonExistingUser")).thenReturn(Optional.empty());

        Optional<User> result = userService.findOneByUsername("nonExistingUser");

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findOneByUsername("nonExistingUser");
    }

    @Test
    void shouldReturnUserIfUsernameFound() {
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findOneByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findOneByUsername("testUser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testUser");
        verify(userRepository, times(1)).findOneByUsername("testUser");
    }
}
