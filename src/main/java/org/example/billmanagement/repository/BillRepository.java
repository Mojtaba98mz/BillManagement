package org.example.billmanagement.repository;

import org.example.billmanagement.model.Bill;
import org.example.billmanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Modifying
    @Query("DELETE FROM Bill b WHERE b.member IN (SELECT m FROM Member m WHERE m.group.id = :groupId)")
    void deleteByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT b FROM Bill b join b.member m join m.group g join g.user u where m.id=:memberId and u.username=:username")
    Page<Bill> findByMemberIdAndUsername(@Param("memberId") Long memberId, @Param("username") String username, Pageable pageable);

    @Query("SELECT b FROM Bill b join b.member m join m.group g join g.user u where b.id=:billId and u.username=:username")
    Optional<Bill> findByBillIdAndUsername(@Param("billId") Long billId, @Param("username") String username);
}
