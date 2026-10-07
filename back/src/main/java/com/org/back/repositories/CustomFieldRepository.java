package com.org.back.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.back.enums.CustomFieldTarget;
import com.org.back.models.CustomField;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {

    List<CustomField> findAllByUser_IdAndTargetOrderById(Long userId, CustomFieldTarget target);

    Optional<CustomField> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndTargetAndNameIgnoreCase(Long userId, CustomFieldTarget target, String name);

    boolean existsByUser_IdAndTargetAndNameIgnoreCaseAndIdNot(Long userId, CustomFieldTarget target, String name, Long id);
}
