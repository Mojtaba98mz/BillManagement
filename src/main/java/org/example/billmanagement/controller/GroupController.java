package org.example.billmanagement.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.service.GroupService;
import org.example.billmanagement.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private static final Logger LOG = LoggerFactory.getLogger(GroupController.class);

    private static final String ENTITY_NAME = "group";

    private final GroupService groupService;

    private final GroupRepository groupRepository;

    public GroupController(GroupService groupService, GroupRepository groupRepository) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
    }

    @PostMapping("")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody GroupDto groupDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.save(groupDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Group group) {
        LOG.debug("REST request to update Group : {}, {}", id, group);
        if (group.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, group.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping("")
    public ResponseEntity<List<Group>> getAllGroups(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Groups");
        Page<Group> page = groupService.findAll(pageable);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Group : {}", id);
        Optional<Group> group = groupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Group : {}", id);
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
