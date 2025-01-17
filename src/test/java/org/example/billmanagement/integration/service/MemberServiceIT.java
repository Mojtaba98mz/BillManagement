package org.example.billmanagement.integration.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceIT {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    private Member member;
    private Group group;
    private MemberDto memberDto;

    private User user;

    @BeforeEach
    public void setup() {
        member = new Member();
        member.setName("John Doe");

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

        memberDto = new MemberDto();
        memberDto.setName("John Doe");
        memberDto.setGroupId(group.getId());
    }

    @Test
    public void testSaveMember_Success() {
        Member savedMember = memberService.save(memberDto);

        assertNotNull(savedMember);
        assertNotNull(savedMember.getId());
        assertEquals(memberDto.getName(), savedMember.getName());
        assertEquals(group.getId(), savedMember.getGroup().getId());

        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
        assertTrue(foundMember.isPresent());
        assertEquals(savedMember.getName(), foundMember.get().getName());
    }

    @Test
    public void testSaveMember_GroupNotFound() {
        memberDto.setGroupId(999L); // Invalid group ID

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            memberService.save(memberDto);
        });

        assertEquals("Entity Group not found", exception.getMessage());

        assertEquals(0, memberRepository.count());
    }

    @Test
    @Rollback
    @WithMockUser("testuser")
    public void testUpdateMember() {
        Member savedMember = memberRepository.save(member);
        savedMember.setName("Jane Doe");

        Member updatedMember = memberService.update(group.getId(), savedMember);

        assertEquals(savedMember.getId(), updatedMember.getId());
        assertEquals("Jane Doe", updatedMember.getName());

        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
        assertTrue(foundMember.isPresent());
        assertEquals("Jane Doe", foundMember.get().getName());
    }

    @Test
    @Rollback
    @WithMockUser("testuser")
    public void testFindAllMembers() {
        member.setGroup(group);
        memberRepository.save(member);

        Member anotherMember = new Member();
        anotherMember.setName("Jane Doe");
        anotherMember.setGroup(group);
        memberRepository.save(anotherMember);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Member> membersPage = memberService.findAll(memberDto.getGroupId(), pageable);

        assertEquals(2, membersPage.getTotalElements());
        assertThat(membersPage.getContent()).extracting(Member::getName).containsExactly("John Doe", "Jane Doe");
    }

    @Test
    @Rollback
    public void testFindOneMember() {
        Member savedMember = memberRepository.save(member);

        Optional<Member> foundMember = memberService.findOne(savedMember.getId());

        assertTrue(foundMember.isPresent());
        assertEquals(savedMember.getId(), foundMember.get().getId());
        assertEquals("John Doe", foundMember.get().getName());
    }

    @Test
    @Rollback
    public void testDeleteMember() {
        Member savedMember = memberRepository.save(member);

        memberService.delete(savedMember.getId());

        Optional<Member> foundMember = memberRepository.findById(savedMember.getId());
        assertFalse(foundMember.isPresent());
    }
}
