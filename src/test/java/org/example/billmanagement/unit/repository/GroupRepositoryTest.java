package org.example.billmanagement.unit.repository;

import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupRepositoryTest {

    @Mock
    private GroupRepository groupRepository;


    @Test
    void testFindByUsername() {
        String username = "testuser";
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setUsername(username);

        Group group1 = new Group();
        group1.setId(1L);
        group1.setTitle("Group 1");
        group1.setUser(user);

        Group group2 = new Group();
        group2.setId(2L);
        group2.setTitle("Group 2");
        group2.setUser(user);

        Page<Group> mockPage = new PageImpl<>(List.of(group1, group2), pageable, 2);
        when(groupRepository.findByUsername(username, pageable)).thenReturn(mockPage);

        Page<Group> result = groupRepository.findByUsername(username, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Group 1", result.getContent().get(0).getTitle());
        assertEquals("Group 2", result.getContent().get(1).getTitle());
        verify(groupRepository, times(1)).findByUsername(username, pageable);
    }
}