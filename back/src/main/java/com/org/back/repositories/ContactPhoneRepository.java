package com.org.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.back.enums.PhoneType;
import com.org.back.models.ContactPhone;

@Repository
public interface ContactPhoneRepository extends JpaRepository<ContactPhone, Long> {

      @Query("SELECT COUNT(cp) > 0 FROM ContactPhone cp " +
                  "JOIN cp.contact c " +
                  "WHERE c.user.id = :userId " +
                  "AND cp.phone = :phone " +
                  "AND c.id != :excludeContactId")
      boolean existsByUserIdAndPhoneExcludingContact(
                  @Param("userId") Long userId,
                  @Param("phone") String phone,
                  @Param("excludeContactId") Long excludeContactId);

      boolean existsByContactFirstNameAndContactLastName(String firstName, String lastName);

      boolean existsByContactIdAndPhoneAndType(Long contactId, String phone, PhoneType type);

}
