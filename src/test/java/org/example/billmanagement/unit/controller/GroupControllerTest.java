package org.example.billmanagement.unit.controller;

import org.example.billmanagement.controller.GroupController;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.service.GroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

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
    public void testUpdateGroup_Success() {
        Group group = new Group();
        group.setId(1L);

        when(groupService.update(any(Group.class))).thenReturn(group);

        ResponseEntity<Group> response = groupController.updateGroup(group);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(group, response.getBody());

        verify(groupService, times(1)).update(any(Group.class));
    }

    @Test
    void testGetAllGroups() {
        Pageable pageable = Pageable.unpaged();
        Page<Group> page = new PageImpl<>(Collections.singletonList(new Group()));
        when(groupService.findAll(pageable)).thenReturn(page);

        ResponseEntity<List<Group>> response = groupController.getAllGroups(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(groupService, times(1)).findAll(pageable);
    }

    @Test
    void testGetGroup_Success() {
        Long id = 1L;
        Group group = new Group();
        when(groupService.findOne(id)).thenReturn(Optional.of(group));

        ResponseEntity<Group> response = groupController.getGroup(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(group, response.getBody());
        verify(groupService, times(1)).findOne(id);
    }

    @Test
    void testGetGroup_NotFound() {
        Long id = 1L;
        when(groupService.findOne(id)).thenReturn(Optional.empty());

        ResponseEntity<Group> response = groupController.getGroup(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(groupService, times(1)).findOne(id);
    }

    @Test
    void testDeleteGroup() {
        Long id = 1L;

        ResponseEntity<Void> response = groupController.deleteGroup(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(groupService, times(1)).delete(id);
    }
}