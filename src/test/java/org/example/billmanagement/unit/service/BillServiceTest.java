package org.example.billmanagement.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.impl.BillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private BillDto billDto;
    private Member member;
    private Bill bill;

    @BeforeEach
    void setUp() {
        billDto = new BillDto();
        billDto.setAmount(100.0F);
        billDto.setMemberId(1L);

        member = new Member();
        member.setId(1L);
        member.setName("John Doe");

        bill = new Bill();
        bill.setAmount(100.0F);
        bill.setMember(member);
    }

    @Test
    public void testSaveBill_WhenMemberExists() {
        when(memberRepository.findById(billDto.getMemberId())).thenReturn(Optional.of(member));
        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        Bill savedBill = billService.save(billDto);

        assertNotNull(savedBill);
        assertEquals(billDto.getAmount(), savedBill.getAmount());
        assertEquals(member, savedBill.getMember());

        verify(memberRepository, times(1)).findById(billDto.getMemberId());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    public void testSaveBill_WhenMemberDoesNotExist() {
        when(memberRepository.findById(billDto.getMemberId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            billService.save(billDto);
        });

        assertEquals("Entity User not found", exception.getMessage());

        verify(memberRepository, times(1)).findById(billDto.getMemberId());
        verify(billRepository, never()).save(any(Bill.class));
    }

    @Test
    void testUpdate() {
        when(billRepository.save(bill)).thenReturn(bill);

        Bill updatedBill = billService.update(bill);

        assertNotNull(updatedBill);
        assertEquals(bill.getId(), updatedBill.getId());
        assertEquals(bill.getAmount(), updatedBill.getAmount());
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<Bill> billPage = mock(Page.class);
        when(billRepository.findAll(pageable)).thenReturn(billPage);

        Page<Bill> result = billService.findAll(pageable);

        assertNotNull(result);
        assertEquals(billPage, result);
        verify(billRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindOne() {
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        Optional<Bill> foundBill = billService.findOne(1L);

        assertTrue(foundBill.isPresent());
        assertEquals(bill.getId(), foundBill.get().getId());
        assertEquals(bill.getAmount(), foundBill.get().getAmount());
        verify(billRepository, times(1)).findById(1L);
    }

    @Test
    void testFindOne_NotFound() {
        when(billRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Bill> foundBill = billService.findOne(1L);

        assertFalse(foundBill.isPresent());
        verify(billRepository, times(1)).findById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(billRepository).deleteById(1L);

        billService.delete(1L);

        verify(billRepository, times(1)).deleteById(1L);
    }
}