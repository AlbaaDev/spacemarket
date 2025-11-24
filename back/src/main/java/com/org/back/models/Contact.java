package com.org.back.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @NotBlank(message = "Last name cannot be blank")
    @Column(nullable = false, length = 45)
    private String firstName; 

    @NotBlank(message = "First name cannot be blank")
    @Column(nullable = false, length = 45)
    private String lastName;

    @NotBlank(message = "Phone cannot be blank")
    @Size(min = 10, max = 12, message = "Phone number should be between 10 and 12")
    private String phone;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Birth date cannot be null")
    @Column(nullable = false, length = 12)
    private LocalDate birthDate;

    @NotBlank(message = "City cannot be blank")
    @Column(nullable = false, length = 45)
    private String city;

    @NotBlank(message = "Adress cannot be blank")
    @Column(nullable = false, length = 45)
    private String adress;

    @NotBlank(message = "Country cannot be blank")
    @Column(nullable = false, length = 45)
    private String country;

    // @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Opportunity> opportunities = new ArrayList<>();

    // public void addOpportunity(Opportunity opportunity) {
    //     opportunities.add(opportunity);
    //     opportunity.setContact(this);
    // }

    // public void removeOpportunity(Opportunity opportunity) {
    //     opportunities.remove(opportunity);
    //     opportunity.setContact(null);
    // }

    @ManyToMany
    @JoinTable(
        name = "contact_opportunity",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "opportunity_id")
    )
    private Set<Opportunity> opportunities = new HashSet<>();
}
