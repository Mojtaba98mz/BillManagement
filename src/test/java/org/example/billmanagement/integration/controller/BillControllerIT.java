package org.example.billmanagement.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    private Bill bill;
    private Member member;
    private BillDto billDto;

    @BeforeEach
    public void setup() {
        // Clear the database before each test
        billRepository.deleteAll();
        memberRepository.deleteAll();

        User user = User.builder().username("testuser")
                .firstName("F")
                .lastName("L")
                .password("p".repeat(60))
                .build();
        userRepository.save(user);
        Group group = new Group();
        group.setTitle("Test Group");
        group.setUser(user);
        group = groupRepository.save(group);

        // Create and save a Member entity
        member = new Member();
        member.setName("John Doe");
        member.setGroup(group);
        member = memberRepository.save(member);

        // Create a test bill
        bill = new Bill();
        bill.setAmount(100.0F);
        bill.setMember(member);
        bill = billRepository.save(bill); // Save to the actual database

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

        mockMvc.perform(put("/api/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bill.getId()))
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    public void testGetAllBills() throws Exception {
        mockMvc.perform(get("/api/bills")
                        .param("memberId", member.getId().toString())
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
}