package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_STUDY_ROOM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoom {

    @Id
    @Column(name = "ROOM_ID")
    private Long id;

    @Column(name = "CAPACITY", nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "studyRoom")
    private Set<Reservation> reservations = new HashSet<>();

    // Utility method to check if room is available for a given time period
    public boolean isAvailable(LocalDateTime start, LocalDateTime end) {
        for (Reservation reservation : reservations) {
            // Check if there's an overlap
            if (!(end.isBefore(reservation.getStartDateTime()) ||
                    start.isAfter(reservation.getEndDateTime()))) {
                return false;
            }
        }
        return true;
    }

    // Utility method to get current or upcoming reservation
    public Reservation getCurrentOrUpcomingReservation() {
        LocalDateTime now = LocalDateTime.now();
        return reservations.stream()
                .filter(r -> r.getEndDateTime().isAfter(now))
                .min((r1, r2) -> r1.getStartDateTime().compareTo(r2.getStartDateTime()))
                .orElse(null);
    }
}