package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_EXHIBITION")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Exhibition extends Event {

    @Column(name = "EXPENSES", nullable = false)
    private Double expenses;

    @OneToMany(mappedBy = "exhibition")
    private Set<ExhibitionAttendance> attendances = new HashSet<>();

    // Constructor that sets event type
    public Exhibition(Long id, String name, java.time.LocalDateTime startDateTime,
                      java.time.LocalDateTime endDateTime, Integer attendeeCount, Double expenses) {
        super(id, name, startDateTime, endDateTime, attendeeCount, "E");
        this.expenses = expenses;
    }

    // Utility method to get actual attendance count
    public int getActualAttendanceCount() {
        return attendances.size();
    }
}