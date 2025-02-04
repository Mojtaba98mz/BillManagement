package org.example.billmanagement.service;

import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BillService {

    Bill save(BillDto billDto);

    Bill update(Bill bill);

    Page<Bill> findAll(Long memberId, Pageable pageable);

    Optional<Bill> findOne(Long id);

    void delete(Long id);
}
