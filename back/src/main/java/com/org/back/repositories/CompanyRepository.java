package com.org.back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.back.models.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    List<Company> findAllByContacts_Id(Long contactId);
    List<Company> findAllByUser_Id(Long userId);
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.contacts WHERE c.user.id = :userId")
    List<Company> findAllByUser_IdWithContacts(@Param("userId") Long userId);
}
