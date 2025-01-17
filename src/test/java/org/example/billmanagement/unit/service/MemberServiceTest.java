package org.example.billmanagement.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private MemberDto memberDto;
    private Group group;
    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("John Doe");

        memberDto = new MemberDto();
        memberDto.setName("John Doe");
        memberDto.setGroupId(1L);

        group = new Group();
        group.setId(1L);

        member = new Member();
        member.setName("John Doe");
        member.setGroup(group);
    }

    @Test
    public void testSaveMember_WhenGroupExists() {
        when(groupRepository.findById(memberDto.getGroupId())).thenReturn(Optional.of(group));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.save(memberDto);

        assertNotNull(savedMember);
        assertEquals(memberDto.getName(), savedMember.getName());
        assertEquals(group, savedMember.getGroup());

        verify(groupRepository, times(1)).findById(memberDto.getGroupId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    public void testSaveMember_WhenGroupDoesNotExist() {
        when(groupRepository.findById(memberDto.getGroupId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            memberService.save(memberDto);
        });

        assertEquals("Entity Group not found", exception.getMessage());

        verify(groupRepository, times(1)).findById(memberDto.getGroupId());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testUpdate() {
        when(memberRepository.save(member)).thenReturn(member);

        Member updatedMember = memberService.update(member);

        assertNotNull(updatedMember);
        assertEquals(member.getId(), updatedMember.getId());
        assertEquals(member.getName(), updatedMember.getName());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        Page<Member> memberPage = new PageImpl<>(Collections.singletonList(member));
        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        Page<Member> result = memberService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(member, result.getContent().get(0));
        verify(memberRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindOne() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Optional<Member> foundMember = memberService.findOne(1L);

        assertTrue(foundMember.isPresent());
        assertEquals(member.getId(), foundMember.get().getId());
        assertEquals(member.getName(), foundMember.get().getName());
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(memberRepository).deleteById(1L);

        memberService.delete(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }
}