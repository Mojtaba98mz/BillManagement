package org.example.billmanagement.repository;

import org.example.billmanagement.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g JOIN g.user u WHERE u.username = :username")
    Page<Group> findByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT g FROM Group g JOIN g.user u WHERE g.id = :groupId AND u.username = :username")
    Optional<Group> findByGroupIdAndUsername(@Param("groupId") Long groupId, @Param("username") String username);
}
