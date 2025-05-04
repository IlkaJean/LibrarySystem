package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_AUTHOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @Column(name = "AUTHOR_ID")
    private Long id;

    @Column(name = "FNAME", nullable = false)
    private String firstName;

    @Column(name = "LNAME", nullable = false)
    private String lastName;

    @Column(name = "STREET", nullable = false)
    private String street;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "POSTAL_CODE", nullable = false)
    private Integer postalCode;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<SeminarAttendance> seminarAttendances = new HashSet<>();

    // Utility method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}