package com.org.back.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@ToString
@Setter
@Getter
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String email;
}
