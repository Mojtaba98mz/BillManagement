package org.example.billmanagement.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupDto {

    @NotNull
    private String title;

}
