package org.example.billmanagement.service;

import org.example.billmanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberService {

    Member save(Member member);

    Member update(Member member);

    Page<Member> findAll(Pageable pageable);

    Optional<Member> findOne(Long id);

    void delete(Long id);
}
