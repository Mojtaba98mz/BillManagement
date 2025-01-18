package org.example.billmanagement.unit.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.impl.MemberServiceImpl;
import org.example.billmanagement.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

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

    @Mock
    private SecurityUtils securityUtils;

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
    public void testUpdate_Success() {
        // Arrange
        Long groupId = 1L;
        Member member = new Member();
        member.setId(1L);
        Group group = new Group();
        group.setId(groupId);
        String username = "testUser";

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(memberRepository.existsById(member.getId())).thenReturn(true);

        when(securityUtils.getCurrentUsername()).thenReturn(username);

        when(groupRepository.findByGroupIdAndUsername(groupId, username)).thenReturn(Optional.of(group));
        when(memberRepository.save(member)).thenReturn(member);

        Member result = memberService.update(groupId, member);

        assertNotNull(result);
        assertEquals(group, result.getGroup());
        verify(groupRepository, times(1)).findById(groupId);
        verify(memberRepository, times(1)).existsById(member.getId());
        verify(groupRepository, times(1)).findByGroupIdAndUsername(groupId, username);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    public void testUpdate_GroupNotFound() {
        Long groupId = 1L;
        Member member = new Member();
        member.setId(1L);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(BadRequestAlertException.class, () -> {
            memberService.update(groupId, member);
        });

        verify(groupRepository, times(1)).findById(groupId);
        verify(memberRepository, never()).existsById(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    public void testUpdate_MemberNotFound() {
        Long groupId = 1L;
        Member member = new Member();
        member.setId(1L);
        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(memberRepository.existsById(member.getId())).thenReturn(false);

        assertThrows(BadRequestAlertException.class, () -> {
            memberService.update(groupId, member);
        });

        verify(groupRepository, times(1)).findById(groupId);
        verify(memberRepository, times(1)).existsById(member.getId());
        verify(memberRepository, never()).save(any());
    }

    @Test
    public void testUpdate_AccessDenied() {
        Long groupId = 1L;
        Member member = new Member();
        member.setId(1L);
        Group group = new Group();
        group.setId(groupId);
        String username = "testUser";

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(memberRepository.existsById(member.getId())).thenReturn(true);

        when(securityUtils.getCurrentUsername()).thenReturn(username);

        when(groupRepository.findByGroupIdAndUsername(groupId, username)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> {
            memberService.update(groupId, member);
        });

        verify(groupRepository, times(1)).findById(groupId);
        verify(memberRepository, times(1)).existsById(member.getId());
        verify(groupRepository, times(1)).findByGroupIdAndUsername(groupId, username);
        verify(memberRepository, never()).save(any());

    }

    @Test
    public void testFindAll_Success() {
        Long groupId = 1L;
        String username = "testUser";
        Pageable pageable = mock(Pageable.class);
        Page<Member> expectedPage = new PageImpl<>(Collections.emptyList());

        when(securityUtils.getCurrentUsername()).thenReturn(username);
        when(groupRepository.findByGroupIdAndUsername(groupId, username)).thenReturn(Optional.of(new Group()));
        when(memberRepository.findAllByGroupId(groupId, pageable)).thenReturn(expectedPage);

        Page<Member> result = memberService.findAll(groupId, pageable);

        assertNotNull(result);
        assertEquals(expectedPage, result);
        verify(groupRepository, times(1)).findByGroupIdAndUsername(groupId, username);
        verify(memberRepository, times(1)).findAllByGroupId(groupId, pageable);
    }

    @Test
    public void testFindAll_AccessDenied() {
        Long groupId = 1L;
        String username = "testuser";
        Pageable pageable = mock(Pageable.class);

        when(securityUtils.getCurrentUsername()).thenReturn(username);
        when(groupRepository.findByGroupIdAndUsername(groupId, username)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> {
            memberService.findAll(groupId, pageable);
        });

        verify(groupRepository, times(1)).findByGroupIdAndUsername(groupId, username);
        verify(memberRepository, never()).findAllByGroupId(any(), any());
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
        when(securityUtils.getCurrentUsername()).thenReturn("testuser");
        when(memberRepository.findByMemberIdAndUsername(1L,"testuser")).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).deleteById(1L);

        memberService.delete(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }
}