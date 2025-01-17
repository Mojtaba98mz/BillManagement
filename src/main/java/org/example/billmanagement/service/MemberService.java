package org.example.billmanagement.service;

import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberService {

    Member save(MemberDto memberDto);

    Member update(Long groupId, Member member);

    Page<Member> findAll(Long groupId, Pageable pageable);

    Optional<Member> findOne(Long id);

    void delete(Long id);
}
