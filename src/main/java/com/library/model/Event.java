package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PJI_EVENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {

    @Id
    @Column(name = "EVENT_ID")
    private Long id;

    @Column(name = "EVENT_NAME", nullable = false)
    private String name;

    @Column(name = "START_DT", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "END_DT", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "ATTD_NO", nullable = false)
    private Integer attendeeCount;

    @Column(name = "EVENT_TYPE", nullable = false)
    private String eventType;

    // Discriminator check constraint handled by database

    // Utility methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startDateTime) && now.isBefore(endDateTime);
    }

    public boolean isUpcoming() {
        return LocalDateTime.now().isBefore(startDateTime);
    }

    public boolean isPast() {
        return LocalDateTime.now().isAfter(endDateTime);
    }
}