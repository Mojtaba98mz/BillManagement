package org.example.billmanagement.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser")
public class BillControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Bill bill;
    private Member member;
    private BillDto billDto;

    @BeforeEach
    public void setup() {
        // Clear the database before each test
        billRepository.deleteAll();
        memberRepository.deleteAll();

        // Create a test bill
        bill = new Bill();
        bill.setAmount(100.0F);
        bill = billRepository.save(bill); // Save to the actual database

        // Create and save a Member entity
        member = new Member();
        member.setName("John Doe");
        member = memberRepository.save(member);

        // Create a BillDto
        billDto = new BillDto();
        billDto.setAmount(100.0F);
        billDto.setMemberId(member.getId());
    }

    @Test
    public void testCreateBill() throws Exception {
        BillDto newBillDto = new BillDto();
        newBillDto.setAmount(200.0F);
        newBillDto.setMemberId(member.getId());

        mockMvc.perform(post("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBillDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(200.0));
    }

    @Test
    public void testUpdateBill() throws Exception {
        bill.setAmount(150.0F);

        mockMvc.perform(put("/api/bills/{id}", bill.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    public void testGetAllBills() throws Exception {
        mockMvc.perform(get("/api/bills")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bill.getId()))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    public void testGetBill() throws Exception {
        mockMvc.perform(get("/api/bills/{id}", bill.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    public void testDeleteBill() throws Exception {
        mockMvc.perform(delete("/api/bills/{id}", bill.getId()))
                .andExpect(status().isNoContent());

        // Verify the bill is deleted
        mockMvc.perform(get("/api/bills/{id}", bill.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateBillWithInvalidIdShouldFail() throws Exception {
        bill.setId(999L); // ID mismatch

        mockMvc.perform(put("/api/bills/{id}", 1L) // Path variable ID is 1
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bill)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateBillWithNonExistingIdShouldFail() throws Exception {
        bill.setId(999L); // Non-existing ID

        mockMvc.perform(put("/api/bills/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billDto)))
                .andExpect(status().isBadRequest());
    }
}