package org.example.billmanagement.service;

import org.example.billmanagement.controller.vm.UserDto;
import org.example.billmanagement.model.Role;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.RoleRepository;
import org.example.billmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    public User createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername().toLowerCase());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encryptedPassword);
        Set<String> defaultAuthorities = new HashSet<>();
        defaultAuthorities.add("ROLE_USER");
        Set<Role> authorities = defaultAuthorities
                .stream()
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        user.setAuthorities(authorities);
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public Optional<User> findOneByUsername(String username) {
        return userRepository.findOneByUsername(username);
    }

}
