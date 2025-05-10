package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "PJI_PAYMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(name = "PAYMENT_ID")
    private Long id;

    @Column(name = "PAYMENT_DATE", nullable = false)
    private java.time.LocalDate paymentDate;

    @Column(name = "PAY_METHOD", nullable = false)
    private String paymentMethod;

    @Column(name = "CARDHOLDER_NAME")
    private String cardholderName;

    @Column(name = "PAYMENT_AMT", nullable = false)
    private Double paymentAmount;

    public Long getId() {
        return id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    @ManyToOne
    @JoinColumn(name = "PJI_INVOICE_INVOICE_ID")
    private Invoice invoice;


}
