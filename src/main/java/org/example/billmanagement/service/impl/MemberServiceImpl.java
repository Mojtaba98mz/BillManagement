package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.service.MemberService;
import org.example.billmanagement.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final GroupRepository groupRepository;

    @Override
    public Member save(MemberDto memberDto) {
        log.debug("Request to save Member : {}", memberDto);

        Group group = groupRepository.findById(memberDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Entity Group not found"));
        Member member = Member.builder().name(memberDto.getName())
                .group(group).build();
        return memberRepository.save(member);
    }

    @Override
    public Member update(Long groupId, Member member) {
        log.debug("Request to update Member : {}", member);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BadRequestAlertException("Entity not found", "group", "idnotfound"));
        if (!memberRepository.existsById(member.getId())) {
            throw new BadRequestAlertException("Entity not found", "member", "idnotfound");
        }
        // check user has right access
        groupRepository.findByGroupIdAndUsername(groupId, SecurityUtils.getCurrentUsername())
                        .orElseThrow(()-> new AccessDeniedException("IllegalAccess"));
        member.setGroup(group);
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> findAll(Long groupId, Pageable pageable) {
        log.debug("Request to get all Members");
        groupRepository.findByGroupIdAndUsername(groupId, SecurityUtils.getCurrentUsername())
                .orElseThrow(()-> new AccessDeniedException("IllegalAccess"));
        return memberRepository.findAllByGroupId(groupId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findOne(Long id) {
        log.debug("Request to get Member : {}", id);
        return memberRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Member : {}", id);
        memberRepository.deleteById(id);
    }
}
