package org.example.billmanagement.service.impl;

import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member save(Member member) {
        LOG.debug("Request to save Member : {}", member);
        return memberRepository.save(member);
    }

    @Override
    public Member update(Member member) {
        LOG.debug("Request to update Member : {}", member);
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> findAll(Pageable pageable) {
        LOG.debug("Request to get all Members");
        return memberRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findOne(Long id) {
        LOG.debug("Request to get Member : {}", id);
        return memberRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Member : {}", id);
        memberRepository.deleteById(id);
    }
}
