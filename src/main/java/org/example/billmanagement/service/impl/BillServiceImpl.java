package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    private final MemberRepository memberRepository;

    @Override
    public Bill save(BillDto billDto) {
        log.debug("Request to save Bill : {}", billDto);
        Member member = memberRepository.findById(billDto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Entity User not found"));
        Bill bill = Bill.builder().amount(billDto.getAmount())
                .member(member).build();
        return billRepository.save(bill);
    }

    @Override
    public Bill update(Bill bill) {
        log.debug("Request to update Bill : {}", bill);
        return billRepository.save(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bill> findAll(Pageable pageable) {
        log.debug("Request to get all Bills");
        return billRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bill> findOne(Long id) {
        log.debug("Request to get Bill : {}", id);
        return billRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Bill : {}", id);
        billRepository.deleteById(id);
    }
}
