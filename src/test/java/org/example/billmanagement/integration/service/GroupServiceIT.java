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
    void testSaveGroup() {
        GroupDto groupDto = new GroupDto();
        groupDto.setTitle("Test Group");
        groupDto.setUserId(testUser.getId());

        Group savedGroup = groupService.save(groupDto);

        assertNotNull(savedGroup.getId());
        assertEquals("Test Group", savedGroup.getTitle());
        assertEquals(testUser.getId(), savedGroup.getUser().getId());

        Optional<Group> foundGroup = groupRepository.findById(savedGroup.getId());
        assertTrue(foundGroup.isPresent());
        assertEquals("Test Group", foundGroup.get().getTitle());
    }

    @Test
    void testSaveGroup_UserNotFound() {
        GroupDto groupDto = new GroupDto();
        groupDto.setTitle("Test Group");
        groupDto.setUserId(999L); // Non-existent user ID

        assertThrows(EntityNotFoundException.class, () -> groupService.save(groupDto));
    }

    @Test
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
}