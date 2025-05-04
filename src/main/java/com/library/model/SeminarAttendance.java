package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_SEMINAR_ATTD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeminarAttendance {

    @Id
    @Column(name = "INVITATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PJI_AUTHOR_AUTHOR_ID")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "PJI_SEMINAR_EVENT_ID", nullable = false)
    private Seminar seminar;
}