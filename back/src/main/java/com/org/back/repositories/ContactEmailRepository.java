package com.org.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.back.enums.EmailType;
import com.org.back.models.ContactEmail;

@Repository
public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {

        @Query("SELECT COUNT(ce) > 0 FROM ContactEmail ce " +
                        "JOIN ce.contact c " +
                        "WHERE c.user.id = :userId " +
                        "AND ce.email = :email " +
                        "AND c.id != :excludeContactId")
        boolean existsByUserIdAndEmailExcludingContact(
                        @Param("userId") Long userId,
                        @Param("email") String email,
                        @Param("excludeContactId") Long excludeContactId);

        boolean existsByContactFirstNameAndContactLastName(String firstName, String lastName);
        boolean existsByContactIdAndEmailAndType(Long contactId, String email, EmailType type);
}