package org.example.billmanagement.integration.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GroupServiceIT {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        groupRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFirstName("F");
        testUser.setLastName("L");
        testUser.setPassword("p".repeat(60));
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser("testuser")
    void testSaveGroup() {
        GroupDto groupDto = new GroupDto();
        groupDto.setTitle("Test Group");

        Group savedGroup = groupService.save(groupDto);

        assertNotNull(savedGroup.getId());
        assertEquals("Test Group", savedGroup.getTitle());
        assertEquals(testUser.getId(), savedGroup.getUser().getId());

        Optional<Group> foundGroup = groupRepository.findById(savedGroup.getId());
        assertTrue(foundGroup.isPresent());
        assertEquals("Test Group", foundGroup.get().getTitle());
    }

    @Test
    @WithMockUser("testuser")
    void testUpdateGroup() {
        Group group = new Group();
        group.setTitle("Initial Title");
        group.setUser(testUser);
        group = groupRepository.save(group);

        group.setTitle("Updated Title");
        Group updatedGroup = groupService.update(group);

        assertEquals("Updated Title", updatedGroup.getTitle());

        Optional<Group> foundGroup = groupRepository.findById(group.getId());
        assertTrue(foundGroup.isPresent());
        assertEquals("Updated Title", foundGroup.get().getTitle());
    }
    
    @Test
    @WithMockUser("testuser")
    void testFindAllGroups() {
        Group group1 = new Group();
        group1.setTitle("Group 1");
        group1.setUser(testUser);
        groupRepository.save(group1);

        Group group2 = new Group();
        group2.setTitle("Group 2");
        group2.setUser(testUser);
        groupRepository.save(group2);

        Page<Group> groups = groupService.findAll(PageRequest.of(0, 10));

        assertEquals(2, groups.getTotalElements());
        assertThat(groups.getContent()).extracting(Group::getTitle).containsExactlyInAnyOrder("Group 1", "Group 2");
    }

    @Test
    void testFindOneGroup() {
        Group group = new Group();
        group.setTitle("Test Group");
        group.setUser(testUser);
        group = groupRepository.save(group);

        Optional<Group> foundGroup = groupService.findOne(group.getId());

        assertTrue(foundGroup.isPresent());
        assertEquals("Test Group", foundGroup.get().getTitle());
    }

    @Test
    void testFindOneGroup_NotFound() {
        Optional<Group> foundGroup = groupService.findOne(999L); // Non-existent group ID
        assertFalse(foundGroup.isPresent());
    }

    @Test
    void testDeleteGroup() {
        Group group = new Group();
        group.setTitle("Test Group");
        group.setUser(testUser);
        group = groupRepository.save(group);

        groupService.delete(group.getId());

        Optional<Group> foundGroup = groupRepository.findById(group.getId());
        assertFalse(foundGroup.isPresent());
    }
}