package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_SEM_SPONSOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeminarSponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "PJI_SEMINAR_EVENT_ID")
    private Seminar seminar;

    @ManyToOne
    @JoinColumn(name = "PJI_SPONSOR_SPONSOR_ID", nullable = false)
    private Sponsor sponsor;
}
