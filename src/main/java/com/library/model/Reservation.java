package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PJI_RESERVATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @Column(name = "RESERVE_ID")
    private Long id;

    @Column(name = "TOPIC_DESC", nullable = false)
    private String topicDescription;

    @Column(name = "START_DT", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "END_DT", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "GROUP_SIZE", nullable = false)
    private Integer groupSize;

    @ManyToOne
    @JoinColumn(name = "PJI_CUSTOMER_CUST_ID")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "PJI_STUDY_ROOM_ROOM_ID")
    private StudyRoom studyRoom;

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

    public String getStatus() {
        if (isActive()) {
            return "In Progress";
        } else if (isUpcoming()) {
            return "Upcoming";
        } else {
            return "Completed";
        }
    }
}