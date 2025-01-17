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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public void testUpdateBill_Success() {
        when(billRepository.existsById(1L)).thenReturn(true);
        when(billService.update(any(Bill.class))).thenReturn(bill);

        ResponseEntity<Bill> response = billController.updateBill(1L, bill);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bill, response.getBody());

        verify(billRepository, times(1)).existsById(1L);
        verify(billService, times(1)).update(any(Bill.class));
    }

    @Test
    public void testUpdateBill_InvalidId() {
        bill.setId(2L); // ID mismatch

        BadRequestAlertException exception = assertThrows(BadRequestAlertException.class, () -> {
            billController.updateBill(1L, bill);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(billRepository, never()).existsById(anyLong());
        verify(billService, never()).update(any(Bill.class));
    }

    @Test
    public void testUpdateBill_EntityNotFound() {
        when(billRepository.existsById(1L)).thenReturn(false);

        BadRequestAlertException exception = assertThrows(BadRequestAlertException.class, () -> {
            billController.updateBill(1L, bill);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(billRepository, times(1)).existsById(1L);
        verify(billService, never()).update(any(Bill.class));
    }

    @Test
    public void testGetAllBills_Success() {
        when(billService.findAll(any(Pageable.class))).thenReturn(billPage);

        ResponseEntity<List<Bill>> response = billController.getAllBills(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(bill, response.getBody().get(0));

        verify(billService, times(1)).findAll(any(Pageable.class));
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