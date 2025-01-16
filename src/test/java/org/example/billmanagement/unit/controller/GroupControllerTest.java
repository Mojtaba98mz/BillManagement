package org.example.billmanagement.unit.controller;

import org.example.billmanagement.controller.GroupController;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGroup() {
        GroupDto groupDto = new GroupDto();
        Group group = new Group();
        when(groupService.save(any(GroupDto.class))).thenReturn(group);

        ResponseEntity<Group> response = groupController.createGroup(groupDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(group, response.getBody());
        verify(groupService, times(1)).save(any(GroupDto.class));
    }

    @Test
    void testUpdateGroup_Success() {
        Long id = 1L;
        Group group = new Group();
        group.setId(id);

        when(groupRepository.existsById(id)).thenReturn(true);

        ResponseEntity<Group> response = groupController.updateGroup(id, group);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(group, response.getBody());
        verify(groupRepository, times(1)).existsById(id);
    }

    @Test
    void testUpdateGroup_InvalidId() {
        Long id = 1L;
        Group group = new Group();
        group.setId(2L);

        assertThrows(BadRequestAlertException.class, () -> groupController.updateGroup(id, group));
    }

    @Test
    void testUpdateGroup_IdNull() {
        Long id = 1L;
        Group group = new Group();

        assertThrows(BadRequestAlertException.class, () -> groupController.updateGroup(id, group));
    }

    @Test
    void testUpdateGroup_EntityNotFound() {
        Long id = 1L;
        Group group = new Group();
        group.setId(id);

        when(groupRepository.existsById(id)).thenReturn(false);

        assertThrows(BadRequestAlertException.class, () -> groupController.updateGroup(id, group));
    }
}