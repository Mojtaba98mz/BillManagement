package org.example.billmanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.User;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.repository.UserRepository;
import org.example.billmanagement.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Group save(GroupDto group) {
        LOG.debug("Request to save Group : {}", group);
        Optional<User> byId = userRepository.findById(group.getUserId());
        if (byId.isEmpty()){
            throw new EntityNotFoundException("Entity User not found");
        }
        Group newGroup = new Group();
        newGroup.setTitle(group.getTitle());
        newGroup.setUser(byId.get());
        return groupRepository.save(newGroup);
    }

    @Override
    public Group update(Group group) {
        LOG.debug("Request to update Group : {}", group);
        return groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Group> findAll(Pageable pageable) {
        LOG.debug("Request to get all Groups");
        return groupRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findOne(Long id) {
        LOG.debug("Request to get Group : {}", id);
        return groupRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Group : {}", id);
        groupRepository.deleteById(id);
    }
}
