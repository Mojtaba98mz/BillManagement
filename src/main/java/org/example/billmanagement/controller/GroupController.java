package org.example.billmanagement.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.billmanagement.controller.dto.GroupDto;
import org.example.billmanagement.controller.exception.BadRequestAlertException;
import org.example.billmanagement.model.Group;
import org.example.billmanagement.repository.GroupRepository;
import org.example.billmanagement.service.GroupService;
import org.example.billmanagement.util.ResponseUtil;
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
@Slf4j
@AllArgsConstructor
public class GroupController {

    private static final String ENTITY_NAME = "group";

    private final GroupService groupService;

    @PostMapping("")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody GroupDto groupDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.save(groupDto));
    }

    @PutMapping("")
    public ResponseEntity<Group> updateGroup(@Valid @RequestBody Group group) {
        log.debug("REST request to update Group : {}", group);
        if (group.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        group = groupService.update(group);
        return ResponseEntity.ok().body(group);
    }

    @GetMapping("")
    public ResponseEntity<List<Group>> getAllGroups(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Groups");
        Page<Group> page = groupService.findAll(pageable);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable("id") Long id) {
        log.debug("REST request to get Group : {}", id);
        Optional<Group> group = groupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        log.debug("REST request to delete Group : {}", id);
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
