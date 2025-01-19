package org.example.billmanagement.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.service.BillService;
import org.example.billmanagement.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@Slf4j
@AllArgsConstructor
public class BillController {

    private static final String ENTITY_NAME = "bill";

    private final BillService billService;

    private final BillRepository billRepository;

    @PostMapping("")
    public ResponseEntity<Bill> createBill(@RequestBody BillDto billDto) {
        log.debug("REST request to save Bill : {}", billDto);
        Bill bill = billService.save(billDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @PutMapping("")
    public ResponseEntity<Bill> updateBill(@RequestBody Bill bill) {
        log.debug("REST request to update Bill : {}, {}", bill.getId(), bill);

        bill = billService.update(bill);
        return ResponseEntity.ok().body(bill);
    }

    @GetMapping("")
    public ResponseEntity<List<Bill>> getAllBills(@RequestParam(value = "memberId") final Long memberId,
                                                  @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Bills");
        Page<Bill> page = billService.findAll(memberId, pageable);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBill(@PathVariable("id") Long id) {
        log.debug("REST request to get Bill : {}", id);
        Optional<Bill> bill = billService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable("id") Long id) {
        log.debug("REST request to delete Bill : {}", id);
        billService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
