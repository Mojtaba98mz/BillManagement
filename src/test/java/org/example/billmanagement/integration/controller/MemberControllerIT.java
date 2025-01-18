package org.example.billmanagement.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.MemberDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.MemberRepository;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser")
public class MemberControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

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

        user = new User();
        user.setUsername("testuser");
        user.setFirstName("F");
        user.setLastName("L");
        user.setPassword("p".repeat(60));
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
    public void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // Create the Member
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isCreated());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo("John Doe");
    }

    @Test
    public void createMemberWithExistingId() throws Exception {
        member.setId(1L);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("testuser")
    public void updateMember() throws Exception {
        // Initialize the database
        member.setGroup(group);
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member
        Member updatedMember = memberRepository.findById(member.getId()).get();
        updatedMember.setName("Jane");

        mockMvc.perform(put("/api/members/{groupId}", group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo("Jane");
    }

    @Test
    public void updateNonExistingMember() throws Exception {
        mockMvc.perform(put("/api/members/{id}", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetAllMembers() throws Exception {
        member.setGroup(group);
        memberRepository.saveAndFlush(member);
        mockMvc.perform(get("/api/members")
                        .param("groupId", group.getId().toString())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[*].name").value(hasItem("John Doe")));
    }

    @Test
    public void getMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get the member
        mockMvc.perform(get("/api/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(member.getId().intValue()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void getNonExistingMember() throws Exception {
        // Get the member
        mockMvc.perform(get("/api/members/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteMember() throws Exception {
        // Initialize the database
        member.setGroup(group);
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeDelete = memberRepository.findAll().size();

        // Delete the member
        mockMvc.perform(delete("/api/members/{id}", member.getId()))
                .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}