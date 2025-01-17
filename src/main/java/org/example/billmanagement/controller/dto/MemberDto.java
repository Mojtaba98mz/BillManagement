package org.example.billmanagement.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberDto {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Long groupId;
}
