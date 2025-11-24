package com.org.back.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Opportunity")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 124)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 124)
    private String businessName;

    @Column(name = "opportunity_value", nullable = false)
    private Long value;

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
