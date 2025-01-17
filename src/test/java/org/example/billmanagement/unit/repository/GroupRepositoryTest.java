package org.example.billmanagement.unit.repository;

import org.example.billmanagement.model.Group;
import org.example.billmanagement.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupRepositoryTest {

    @Mock
    private GroupRepository groupRepository;

    @Test
    public void testFindByUserIsCurrentUser() {
        Group group1 = new Group();
        group1.setId(1L);
        Group group2 = new Group();
        group2.setId(2L);
        List<Group> expectedGroups = Arrays.asList(group1, group2);

        when(groupRepository.findByUserIsCurrentUser()).thenReturn(expectedGroups);

        List<Group> actualGroups = groupRepository.findByUserIsCurrentUser();

        assertEquals(expectedGroups.size(), actualGroups.size());
        assertEquals(expectedGroups, actualGroups);
    }
}