package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_INDIVIDUAL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class IndividualSponsor extends Sponsor {

    @Column(name = "FNAME", nullable = false)
    private String firstName;

    @Column(name = "LNAME", nullable = false)
    private String lastName;

    // Constructor that sets sponsor type
    public IndividualSponsor(Long id, String firstName, String lastName) {
        super(id, "I", null);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Utility method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}