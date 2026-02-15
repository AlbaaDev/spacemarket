package com.org.back.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.back.models.Company;
import com.org.back.models.Contact;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Contact> findByName(String name);
}
