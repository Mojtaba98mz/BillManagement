package org.example.billmanagement.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotBlank
    @Size(min = 4)
    private String password;
}
