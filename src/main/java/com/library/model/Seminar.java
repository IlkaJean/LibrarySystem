package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_SEMINAR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Seminar extends Event {

    @Column(name = "EST_AUTH", nullable = false)
    private Integer estimatedAuthors;

    @OneToMany(mappedBy = "seminar")
    private Set<SeminarAttendance> attendances = new HashSet<>();

    @OneToMany(mappedBy = "seminar")
    private Set<SeminarSponsor> sponsors = new HashSet<>();

    // Constructor that sets event type
    public Seminar(Long id, String name, java.time.LocalDateTime startDateTime,
                   java.time.LocalDateTime endDateTime, Integer attendeeCount, Integer estimatedAuthors) {
        super(id, name, startDateTime, endDateTime, attendeeCount, "S");
        this.estimatedAuthors = estimatedAuthors;
    }

    // Utility method to get actual author attendance count
    public int getActualAuthorCount() {
        return attendances.size();
    }

    // Utility method to get total sponsorship amount
    public double getTotalSponsorshipAmount() {
        return sponsors.stream()
                .mapToDouble(SeminarSponsor::getAmount)
                .sum();
    }
}