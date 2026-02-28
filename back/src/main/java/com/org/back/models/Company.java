package com.org.back.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "Companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = true, length = 45)
    private String country;

    @Column(nullable = true, length = 45)
    private String city;

    @Column(nullable = true, length = 45)
    private String address;

    @Column(nullable = true, length = 45)
    private String industry;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = { CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private List<Contact> contacts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
