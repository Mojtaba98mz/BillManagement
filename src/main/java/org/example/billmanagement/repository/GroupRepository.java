package org.example.billmanagement.repository;

import org.example.billmanagement.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("select g from Group g where g.user.username = ?#{authentication.name}")
    List<Group> findByUserIsCurrentUser();
}
