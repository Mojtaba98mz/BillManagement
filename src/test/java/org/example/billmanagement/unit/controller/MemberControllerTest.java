package org.example.billmanagement.unit.controller;

import org.example.billmanagement.controller.MemberController;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.service.MemberService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MemberDto memberDto;
    private Member member;
    private Page<Member> memberPage;

    @BeforeEach
    public void setUp() {

        memberDto = new MemberDto();
        memberDto.setName("John Doe");
        memberDto.setGroupId(1L);

        member = new Member();
        member.setId(1L);
        member.setName("John Doe");

        List<Member> members = Collections.singletonList(member);
        memberPage = new PageImpl<>(members);
    }

    @Test
    public void testCreateMember_Success() {
        when(memberService.save(any(MemberDto.class))).thenReturn(member);

        ResponseEntity<Member> response = memberController.createMember(memberDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(member, response.getBody());

        verify(memberService, times(1)).save(any(MemberDto.class));
    }

    @Test
    public void testGetAllMembers_Success() {
        when(memberService.findAll(eq(memberDto.getGroupId()), any(Pageable.class))).thenReturn(memberPage);

        ResponseEntity<List<Member>> response = memberController.getAllMembers(memberDto.getGroupId(), Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(member, response.getBody().get(0));

        verify(memberService, times(1)).findAll(eq(memberDto.getGroupId()), any(Pageable.class));
    }

    @Test
    public void testGetMember_Success() {
        when(memberService.findOne(1L)).thenReturn(Optional.of(member));

        ResponseEntity<Member> response = memberController.getMember(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(member, response.getBody());

        verify(memberService, times(1)).findOne(1L);
    }

    @Test
    public void testGetMember_NotFound() {
        when(memberService.findOne(1L)).thenReturn(Optional.empty());

        ResponseEntity<Member> response = memberController.getMember(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(memberService, times(1)).findOne(1L);
    }

    @Test
    public void testDeleteMember_Success() {
        doNothing().when(memberService).delete(1L);

        ResponseEntity<Void> response = memberController.deleteMember(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(memberService, times(1)).delete(1L);
    }
}