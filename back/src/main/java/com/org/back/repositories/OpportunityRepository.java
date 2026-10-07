package com.org.back.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.back.models.Opportunity;

// An Opportunity belongs to the user who owns its principal Contact.
@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    Optional<Opportunity> findByName(String name);

    @Query("select o from Opportunity o join fetch o.principalContact c where c.user.id = :userId order by o.id")
    List<Opportunity> findAllOwnedBy(@Param("userId") Long userId);

    @Query("select o from Opportunity o join fetch o.principalContact c where o.id = :id and c.user.id = :userId")
    Optional<Opportunity> findOwnedBy(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            select coalesce(sum(o.value), 0) from Opportunity o
            where o.principalContact.user.id = :userId
              and o.status = com.org.back.enums.OpportunityStatus.WON
              and o.closeDate between :from and :to
            """)
    long sumRevenue(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select o.closeDate, sum(o.value) from Opportunity o
            where o.principalContact.user.id = :userId
              and o.status = com.org.back.enums.OpportunityStatus.WON
              and o.closeDate between :from and :to
            group by o.closeDate
            """)
    List<Object[]> revenueByDay(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);
}
