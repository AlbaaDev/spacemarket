package com.org.back.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.back.models.Interaction;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findAllByContact_IdAndUser_IdOrderByOccurredOnDescIdDesc(Long contactId, Long userId);

    Optional<Interaction> findByIdAndUser_Id(Long id, Long userId);

    @Query("select count(i) from Interaction i where i.user.id = :userId and i.occurredOn between :from and :to")
    long countInPeriod(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select count(distinct i.contact.id) from Interaction i where i.user.id = :userId and i.occurredOn between :from and :to")
    long countContactsReached(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select i.occurredOn, count(i) from Interaction i
            where i.user.id = :userId and i.occurredOn between :from and :to
            group by i.occurredOn
            """)
    List<Object[]> countByDay(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Modifying
    @Query("delete from Interaction i where i.contact.id = :contactId")
    void deleteAllByContactId(@Param("contactId") Long contactId);

    @Modifying
    @Query("update Interaction i set i.opportunity = null where i.opportunity.id = :opportunityId")
    void detachOpportunity(@Param("opportunityId") Long opportunityId);
}
