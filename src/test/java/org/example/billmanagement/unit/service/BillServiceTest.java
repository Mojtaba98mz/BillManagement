package org.example.billmanagement.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.impl.BillServiceImpl;
import org.example.billmanagement.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private SecurityUtils securityUtils;

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
    void testUpdateBill_Success() {
        // Arrange
        Bill billToUpdate = Bill.builder().id(1L).amount(100F).build();
        String currentUsername = "testUser";

        when(billRepository.existsById(billToUpdate.getId())).thenReturn(true);
        when(securityUtils.getCurrentUsername()).thenReturn(currentUsername);
        Bill existingBill = Bill.builder().id(1L).amount(50F).build();
        when(billRepository.findByBillIdAndUsername(billToUpdate.getId(), currentUsername))
                .thenReturn(Optional.of(existingBill));
        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Bill updatedBill = billService.update(billToUpdate);

        // Assert
        assertEquals(billToUpdate.getAmount(), updatedBill.getAmount());
        verify(billRepository, times(1)).existsById(billToUpdate.getId());
        verify(billRepository, times(1)).findByBillIdAndUsername(billToUpdate.getId(), currentUsername);
        verify(billRepository, times(1)).save(existingBill);
    }

    @Test
    void testUpdateBill_BillNotFound() {
        // Arrange
        Bill billToUpdate = Bill.builder().id(1L).amount(100F).build();

        when(billRepository.existsById(billToUpdate.getId())).thenReturn(false);

        // Act & Assert
        BadRequestAlertException exception = assertThrows(BadRequestAlertException.class, () -> {
            billService.update(billToUpdate);
        });

        verify(billRepository, times(1)).existsById(billToUpdate.getId());
        verify(billRepository, never()).findByBillIdAndUsername(anyLong(), anyString());
        verify(billRepository, never()).save(any());
    }

    @Test
    void testUpdateBill_AccessDenied() {
        // Arrange
        Bill billToUpdate = Bill.builder().id(1L).amount(100F).build();
        String currentUsername = "testUser";

        when(billRepository.existsById(billToUpdate.getId())).thenReturn(true);
        when(securityUtils.getCurrentUsername()).thenReturn(currentUsername);
        when(billRepository.findByBillIdAndUsername(billToUpdate.getId(), currentUsername))
                .thenReturn(Optional.empty());

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            billService.update(billToUpdate);
        });

        assertEquals("IllegalAccess", exception.getMessage());
        verify(billRepository, times(1)).existsById(billToUpdate.getId());
        verify(billRepository, times(1)).findByBillIdAndUsername(billToUpdate.getId(), currentUsername);
        verify(billRepository, never()).save(any());
    }

    @Test
    void testFindAll() {
        when(securityUtils.getCurrentUsername()).thenReturn("testuser");
        Pageable pageable = mock(Pageable.class);
        Page<Bill> billPage = mock(Page.class);
        when(billRepository.findByMemberIdAndUsername(1L,"testuser",pageable)).thenReturn(billPage);

        Page<Bill> result = billService.findAll(1L, pageable);
        assertEquals(billPage, result);
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