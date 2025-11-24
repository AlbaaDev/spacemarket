package com.org.back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.back.models.Opportunity;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    Optional<Opportunity> findByName(String name);
}
