package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_INVOICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @Column(name = "INVOICE_ID")
    private Long id;

    @Column(name = "INVOICE_DATE", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "INVOICE_AMT", nullable = false)
    private Double amount;

    @OneToOne(mappedBy = "invoice")
    private Rental rental;

    @OneToMany(mappedBy = "invoice")
    private Set<Payment> payments = new HashSet<>();

    // Utility methods
    public boolean isPaid() {
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        return totalPaid >= amount;
    }

    public double getBalance() {
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        return amount - totalPaid;
    }
}