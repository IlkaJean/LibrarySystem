package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_EXH_ATTD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionAttendance {

    @Id
    @Column(name = "REG_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PJI_EXHIBITION_EVENT_ID")
    private Exhibition exhibition;

    @ManyToOne
    @JoinColumn(name = "PJI_CUSTOMER_CUST_ID")
    private Customer customer;
}