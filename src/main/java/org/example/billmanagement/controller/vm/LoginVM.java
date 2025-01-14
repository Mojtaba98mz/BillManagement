package org.example.billmanagement.controller.vm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginVM {

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotBlank
    @Size(min = 4)
    private String password;
}
