package org.example.billmanagement.repository;

import org.example.billmanagement.model.Group;
import org.example.billmanagement.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Page<Member> findAllByGroupId(Long groupId, Pageable pageable);

    @Query("SELECT m.id,m.name, COALESCE(SUM(b.amount), 0) " +
            "FROM Member m " +
            "LEFT JOIN m.bills b " +
            "WHERE m.group.id = :groupId " +
            "GROUP BY m.id")
    List<Object[]> findTotalAmountPaidByEachMemberInGroup(@Param("groupId") Long groupId);

    @Query("SELECT m FROM Member m JOIN m.group g JOIN g.user u WHERE m.id = :memberId AND u.username = :username")
    Optional<Member> findByMemberIdAndUsername(@Param("memberId") Long memberId, @Param("username") String username);

    @Modifying
    @Query("DELETE FROM Member m WHERE m.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query("DELETE FROM Bill b WHERE b.member.id = :memberId")
    void deleteBillsByMemberId(@Param("memberId") Long memberId);

}
