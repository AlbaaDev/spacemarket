package com.org.back.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "Company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false, length = 45)
    private String name;

    @NotBlank(message = "Country cannot be blank")
    @Column(nullable = false, length = 45)
    private String country;

    @NotBlank(message = "City cannot be blank")
    @Column(nullable = false, length = 45)
    private String city;

    @NotBlank(message = "Address cannot be blank")
    @Column(nullable = false, length = 45)
    private String address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Contact> contacts;
}
