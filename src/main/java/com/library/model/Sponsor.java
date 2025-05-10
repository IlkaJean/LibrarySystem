package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_SPONSOR")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "SPONSOR_TYPE", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Sponsor {
    @Id
    @Column(name = "SPONSOR_ID")
    private Long id;

    @Column(name = "SPONSOR_TYPE", insertable = false, updatable = false) // let Hibernate manage it
    private String sponsorType;

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeminarSponsor> sponsoredSeminars = new HashSet<>();

    public double getTotalSponsorshipAmount() {
        return sponsoredSeminars.stream()
                .mapToDouble(SeminarSponsor::getAmount)
                .sum();
    }
}
