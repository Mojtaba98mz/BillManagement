package org.example.billmanagement.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.service.MemberService;
import org.example.billmanagement.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@Slf4j
@AllArgsConstructor
public class MemberController {

    private static final String ENTITY_NAME = "member";

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<Member> createMember(@Valid @RequestBody MemberDto memberDto) {
        log.debug("REST request to save Member : {}", memberDto);
        Member member = memberService.save(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Member> updateMember(
        @PathVariable(value = "groupId", required = false) final Long groupId,
        @Valid @RequestBody Member member){
        log.debug("REST request to update Member : {}, {}", member.getId(), member);

        member = memberService.update(groupId,member);
        return ResponseEntity.ok().body(member);
    }

    @GetMapping("")
    public ResponseEntity<List<Member>> getAllMembers(@RequestParam(value = "groupId") final Long groupId,
                                                      @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Members");
        Page<Member> page = memberService.findAll(groupId,pageable);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable("id") Long id) {
        log.debug("REST request to get Member : {}", id);
        Optional<Member> member = memberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(member);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id") Long id) {
        log.debug("REST request to delete Member : {}", id);
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
