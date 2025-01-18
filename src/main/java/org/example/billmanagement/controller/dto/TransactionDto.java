package org.example.billmanagement.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {

    private String from;

    private String to;

    private Double amount;

    @Override
    public String toString() {
        return from + " pays " + to + " $" + String.format("%.2f", amount);
    }

}
