package com.org.back.models;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.ColumnDefault;

import com.org.back.enums.OpportunityStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = { "principalContact", "contacts" })
@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Opportunities")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Custom field values keyed by field id (see docs/adr/0001-custom-field-values-in-jsonb.md). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_values")
    private Map<String, Object> customValues = new HashMap<>();

    public Map<String, Object> getCustomValues() {
        if (customValues == null) {
            customValues = new HashMap<>();
        }
        return customValues;
    }

    @NotBlank
    @Column(nullable = false, length = 124)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 124)
    private String businessName;

    @Column(name = "opportunity_value", nullable = false)
    private Long value;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'OPEN'")
    @Column(nullable = false, length = 8)
    private OpportunityStatus status = OpportunityStatus.OPEN;

    // Set when the Opportunity becomes Won or Lost, cleared when it is reopened.
    private LocalDate closeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact principalContact;

    public void setPrincipalContact(Contact principalContact) {
        this.principalContact = principalContact;
    }

    @ManyToMany(mappedBy =  "opportunities")
    Set<Contact> contacts = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Opportunity))
            return false;
        return id != null && id.equals(((Opportunity) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
