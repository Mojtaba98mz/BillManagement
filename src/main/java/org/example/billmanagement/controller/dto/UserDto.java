package org.example.billmanagement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotNull(message = "username can not be null")
    @NotBlank(message = "username is required")
    @Size(min = 1, max = 50)
    private String username;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull(message = "password is required")
    @NotBlank(message = "password must be at least 4 characters")
    @Size(min = 4, max = 100, message = "password must be at least 4 characters")
    private String password;

}
