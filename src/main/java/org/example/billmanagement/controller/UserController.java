package org.example.billmanagement.controller;

import jakarta.validation.Valid;
import org.example.billmanagement.controller.exception.LoginAlreadyUsedException;
import org.example.billmanagement.controller.dto.UserDto;
import org.example.billmanagement.model.User;
import org.example.billmanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDTO) {
        if (userService.findOneByUsername(userDTO.getUsername().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }
    }
}