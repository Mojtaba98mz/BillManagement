package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.GroupService;
import org.example.billmanagement.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public Group save(GroupDto groupDto) {
        log.debug("Request to save Group : {}", groupDto);
        User user = userRepository.findOneByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new EntityNotFoundException("Entity User not found"));
        Group group = Group.builder().title(groupDto.getTitle())
                .user(user).build();
        return groupRepository.save(group);
    }

    @Override
    public Group update(Group group) {
        log.debug("Request to update Group : {}", group);
        return groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Group> findAll(Pageable pageable) {
        log.debug("Request to get all Groups");
        return groupRepository.findByUsername(SecurityUtils.getCurrentUsername(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findOne(Long id) {
        log.debug("Request to get Group : {}", id);
        return groupRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Group : {}", id);
        groupRepository.deleteById(id);
    }
}
