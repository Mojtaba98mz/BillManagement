package org.example.billmanagement.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillDto {

    @NotNull
    private Float amount;

    @NotNull
    private Long memberId;
}
