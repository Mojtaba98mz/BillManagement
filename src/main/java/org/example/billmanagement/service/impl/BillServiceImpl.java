package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.BillService;
import org.example.billmanagement.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    private final SecurityUtils securityUtils;

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
        // check bill is exist
        if (!billRepository.existsById(bill.getId())) {
            throw new BadRequestAlertException("Entity not found", "bill", "idnotfound");
        }
        // check the right access
        Bill access = billRepository.findByBillIdAndUsername(bill.getId(), securityUtils.getCurrentUsername())
                .orElseThrow(() -> new AccessDeniedException("IllegalAccess"));
        access.setAmount(bill.getAmount());
        return billRepository.save(access);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bill> findAll(Long memberId, Pageable pageable) {
        log.debug("Request to get all Bills");
        return billRepository.findByMemberIdAndUsername(memberId, securityUtils.getCurrentUsername(), pageable);
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
