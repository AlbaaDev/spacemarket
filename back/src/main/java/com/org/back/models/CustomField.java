package com.org.back.models;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.CustomFieldType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Definition of a Custom field. Its values live in the target record's custom_values column,
 * keyed by this field's id (see docs/adr/0001-custom-field-values-in-jsonb.md).
 */
@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Custom_fields", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "target", "name" }))
public class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CustomFieldTarget target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CustomFieldType type;

    /** Choices of a Single choice field; empty for the other types. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<String> options = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
