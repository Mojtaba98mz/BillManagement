package org.example.billmanagement.service;

import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GroupService {

    Group save(GroupDto group);

    Group update(Group group);

    Page<Group> findAll(Pageable pageable);

    Optional<Group> findOne(Long id);

    void delete(Long id);
}
