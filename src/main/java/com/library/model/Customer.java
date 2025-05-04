package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_CUSTOMER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "CUST_ID")
    private Long id;

    @Column(name = "FNAME", nullable = false)
    private String firstName;

    @Column(name = "LNAME", nullable = false)
    private String lastName;

    @Column(name = "PHONE", nullable = false)
    private String phone;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "ID_TYPE", nullable = false)
    private String idType;

    @Column(name = "ID_NO", nullable = false)
    private String idNumber;

    @OneToMany(mappedBy = "customer")
    private Set<Rental> rentals = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<ExhibitionAttendance> exhibitionAttendances = new HashSet<>();

    // Utility method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Utility method to check active rentals
    public long getActiveRentalCount() {
        return rentals.stream()
                .filter(rental -> "Borrowed".equals(rental.getStatus()))
                .count();
    }
}