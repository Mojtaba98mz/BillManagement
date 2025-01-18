package org.example.billmanagement.repository;

import org.example.billmanagement.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Modifying
    @Query("DELETE FROM Bill b WHERE b.member IN (SELECT m FROM Member m WHERE m.group.id = :groupId)")
    void deleteByGroupId(@Param("groupId") Long groupId);
}
