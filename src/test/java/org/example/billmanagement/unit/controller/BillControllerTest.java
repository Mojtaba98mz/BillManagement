package org.example.billmanagement.unit.controller;

import org.example.billmanagement.controller.BillController;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillControllerTest {

    @Mock
    private BillService billService;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillController billController;

    private BillDto billDto;
    private Bill bill;
    private Page<Bill> billPage;

    @BeforeEach
    public void setUp() {
        billDto = new BillDto();
        billDto.setAmount(100.0F);
        billDto.setMemberId(1L);

        bill = new Bill();
        bill.setId(1L);
        bill.setAmount(100.0F);

        List<Bill> bills = Collections.singletonList(bill);
        billPage = new PageImpl<>(bills);
    }

    @Test
    public void testCreateBill_Success() {
        when(billService.save(any(BillDto.class))).thenReturn(bill);

        ResponseEntity<Bill> response = billController.createBill(billDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bill, response.getBody());

        verify(billService, times(1)).save(any(BillDto.class));
    }

    @Test
    void testUpdateBill_Success() {
        when(billService.update(bill)).thenReturn(bill);

        ResponseEntity<Bill> response = billController.updateBill(bill);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(bill, response.getBody());
        verify(billService, times(1)).update(bill);
    }

    @Test
    void testGetAllBills() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Bill> bills = Arrays.asList(
                Bill.builder().id(1L).amount(100F).build(),
                Bill.builder().id(2L).amount(200F).build()
        );
        Page<Bill> billPage = new PageImpl<>(bills, pageable, bills.size());

        when(billService.findAll(memberId, pageable)).thenReturn(billPage);

        ResponseEntity<List<Bill>> response = billController.getAllBills(memberId, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(bills, response.getBody());
        verify(billService, times(1)).findAll(memberId, pageable);
    }

    @Test
    public void testGetBill_Success() {
        when(billService.findOne(1L)).thenReturn(Optional.of(bill));

        ResponseEntity<Bill> response = billController.getBill(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bill, response.getBody());

        verify(billService, times(1)).findOne(1L);
    }

    @Test
    public void testGetBill_NotFound() {
        when(billService.findOne(1L)).thenReturn(Optional.empty());

        ResponseEntity<Bill> response = billController.getBill(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(billService, times(1)).findOne(1L);
    }

    @Test
    public void testDeleteBill_Success() {
        doNothing().when(billService).delete(1L);

        ResponseEntity<Void> response = billController.deleteBill(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(billService, times(1)).delete(1L);
    }
}