package org.example.billmanagement.service;

import org.example.billmanagement.controller.dto.TransactionDto;

import java.util.List;

public interface BillCalculationService {

    List<TransactionDto> calculate(Long groupId);
}
