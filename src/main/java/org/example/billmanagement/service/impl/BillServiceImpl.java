package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
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
public class BillServiceImpl implements BillService {

    private static final Logger LOG = LoggerFactory.getLogger(BillServiceImpl.class);

    private final BillRepository billRepository;

    private final MemberRepository memberRepository;

    public BillServiceImpl(BillRepository billRepository, MemberRepository memberRepository) {
        this.billRepository = billRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Bill save(BillDto billDto) {
        LOG.debug("Request to save Bill : {}", billDto);
        Optional<Member> byId = memberRepository.findById(billDto.getMemberId());
        if (byId.isEmpty()){
            throw new EntityNotFoundException("Entity User not found");
        }
        Bill bill = new Bill();
        bill.setAmount(billDto.getAmount());
        bill.setMember(byId.get());
        return billRepository.save(bill);
    }

    @Override
    public Bill update(Bill bill) {
        LOG.debug("Request to update Bill : {}", bill);
        return billRepository.save(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bill> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bills");
        return billRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bill> findOne(Long id) {
        LOG.debug("Request to get Bill : {}", id);
        return billRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Bill : {}", id);
        billRepository.deleteById(id);
    }
}
