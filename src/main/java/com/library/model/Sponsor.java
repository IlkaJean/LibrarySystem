package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_SPONSOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Sponsor {

    @Id
    @Column(name = "SPONSOR_ID")
    private Long id;

    @Column(name = "SPONSOR_TYPE", nullable = false)
    private String sponsorType;

    @OneToMany(mappedBy = "sponsor")
    private Set<SeminarSponsor> sponsoredSeminars = new HashSet<>();

    // Utility method to get total sponsorship amount
    public double getTotalSponsorshipAmount() {
        return sponsoredSeminars.stream()
                .mapToDouble(SeminarSponsor::getAmount)
                .sum();
    }
}