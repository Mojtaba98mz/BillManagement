package org.example.billmanagement.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExpenseDto {
    private String name;
    private Double amount;
}
