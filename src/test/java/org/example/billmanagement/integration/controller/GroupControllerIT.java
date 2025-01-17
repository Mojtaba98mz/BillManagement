package org.example.billmanagement.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser")
class GroupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        groupRepository.deleteAll();
        User user = new User();
        user.setUsername("testuser");
        user.setFirstName("F");
        user.setLastName("L");
        user.setPassword("p".repeat(60));
        userRepository.save(user);
    }

    @Test
    void testCreateGroup() throws Exception {
        GroupDto groupDto = new GroupDto();
        groupDto.setTitle("Test Group");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(groupDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Group"));

        List<Group> groups = groupRepository.findAll();
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getTitle()).isEqualTo("Test Group");
    }

    @Test
    void testUpdateGroup() throws Exception {
        Group group = new Group();
        group.setTitle("Initial Group");
        group = groupRepository.save(group);

        group.setTitle("Updated Group");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/groups/{id}", group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(group)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Updated Group"));

        Group updatedGroup = groupRepository.findById(group.getId()).orElseThrow();
        assertThat(updatedGroup.getTitle()).isEqualTo("Updated Group");
    }

    @Test
    void testGetAllGroups() throws Exception {
        Group group1 = new Group();
        group1.setTitle("Group 1");
        Group group2 = new Group();
        group2.setTitle("Group 2");
        groupRepository.saveAll(List.of(group1, group2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groups")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Group 1"))
                .andExpect(jsonPath("$[1].title").value("Group 2"));
    }

    @Test
    void testGetGroup() throws Exception {
        Group group = new Group();
        group.setTitle("Test Group");
        group = groupRepository.save(group);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/groups/{id}", group.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Group"));
    }

    @Test
    void testDeleteGroup() throws Exception {
        Group group = new Group();
        group.setTitle("Test Group");
        group = groupRepository.save(group);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/groups/{id}", group.getId()))
                .andExpect(status().isNoContent());

        assertThat(groupRepository.findById(group.getId())).isEmpty();
    }

    @Test
    void testUpdateGroup_InvalidId() throws Exception {
        Group group = new Group();
        group.setId(1L);
        group.setTitle("Test Group");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/groups/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(group)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateGroup_EntityNotFound() throws Exception {
        Group group = new Group();
        group.setId(1L);
        group.setTitle("Test Group");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/groups/{id}", group.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(group)))
                .andExpect(status().isBadRequest());
    }
}