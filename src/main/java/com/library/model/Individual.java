package com.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_INDIVIDUAL")
@PrimaryKeyJoinColumn(name = "SPONSOR_ID")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("I")
public class Individual extends Sponsor {
    @Column(name = "FNAME")
    private String firstName;

    @Column(name = "LNAME")
    private String lastName;

    public Individual(Long id, String firstName, String lastName) {
        super(id, "I", null);
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

