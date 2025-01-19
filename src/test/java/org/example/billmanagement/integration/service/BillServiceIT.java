package org.example.billmanagement.integration.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.BillDto;
import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.BillRepository;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BillServiceIT {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BillService billService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    private Bill bill;
    private Member member;
    private Group group;
    private BillDto billDto;

    @BeforeEach
    public void setup() {
        bill = new Bill();
        bill.setAmount(100.0F);


        User user = User.builder().username("testuser")
                .firstName("F")
                .lastName("L")
                .password("p".repeat(60))
                .build();
        userRepository.save(user);
        group = new Group();
        group.setTitle("Test Group");
        group.setUser(user);
        group = groupRepository.save(group);

        // Create and save a Member entity
        member = new Member();
        member.setName("John Doe");
        member.setGroup(group);
        member = memberRepository.save(member);

        // Create a BillDto
        billDto = new BillDto();
        billDto.setAmount(100.0F);
        billDto.setMemberId(member.getId());
    }

    @Test
    public void testSaveBill_Success() {
        Bill savedBill = billService.save(billDto);

        assertNotNull(savedBill);
        assertNotNull(savedBill.getId());
        assertEquals(billDto.getAmount(), savedBill.getAmount());
        assertEquals(member.getId(), savedBill.getMember().getId());

        // Verify the bill is saved in the database
        Optional<Bill> foundBill = billRepository.findById(savedBill.getId());
        assertTrue(foundBill.isPresent());
        assertEquals(savedBill.getAmount(), foundBill.get().getAmount());
    }

    @Test
    public void testSaveBill_MemberNotFound() {
        billDto.setMemberId(999L); // Invalid member ID

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            billService.save(billDto);
        });

        assertEquals("Entity User not found", exception.getMessage());

        assertEquals(0, billRepository.count());
    }

    @Test
    @Rollback
    @WithMockUser("testuser")
    public void testUpdateBill() {
        bill.setMember(member);
        Bill savedBill = billRepository.save(bill);
        savedBill.setAmount(150.0F);

        Bill updatedBill = billService.update(savedBill);

        assertEquals(savedBill.getId(), updatedBill.getId());
        assertEquals(150.0F, updatedBill.getAmount());

        Optional<Bill> foundBill = billRepository.findById(savedBill.getId());
        assertTrue(foundBill.isPresent());
        assertEquals(150.0F, foundBill.get().getAmount());
    }

    @Test
    @Rollback
    @WithMockUser("testuser")
    public void testFindAllBills() {
        bill.setMember(member);
        billRepository.save(bill);

        Bill anotherBill = new Bill();
        anotherBill.setAmount(200.0F);
        billRepository.save(anotherBill);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Bill> billsPage = billService.findAll(member.getId(),pageable);

        assertEquals(1, billsPage.getTotalElements());
    }

    @Test
    @Rollback
    public void testFindOneBill() {
        Bill savedBill = billRepository.save(bill);

        Optional<Bill> foundBill = billService.findOne(savedBill.getId());

        assertTrue(foundBill.isPresent());
        assertEquals(savedBill.getId(), foundBill.get().getId());
    }

    @Test
    @Rollback
    public void testDeleteBill() {
        Bill savedBill = billRepository.save(bill);

        billService.delete(savedBill.getId());

        Optional<Bill> foundBill = billRepository.findById(savedBill.getId());
        assertFalse(foundBill.isPresent());
    }
}