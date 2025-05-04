package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "PJI_RENTAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @Column(name = "RENTAL_ID")
    private Long id;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "BORROW_DATE", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "EXP_RETURN_DT", nullable = false)
    private LocalDate expectedReturnDate;

    @Column(name = "ACTUAL_RETURN_DT")
    private LocalDate actualReturnDate;

    @ManyToOne
    @JoinColumn(name = "PJI_CUSTOMER_CUST_ID")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "PJI_INVOICE_INVOICE_ID")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "PJI_BOOK_COPY_COPY_ID")
    private BookCopy bookCopy;

    // Utility methods
    public boolean isOverdue() {
        if ("Borrowed".equals(status)) {
            return LocalDate.now().isAfter(expectedReturnDate);
        } else if ("Returned".equals(status) && actualReturnDate != null) {
            return actualReturnDate.isAfter(expectedReturnDate);
        }
        return false;
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }

        if ("Borrowed".equals(status)) {
            return ChronoUnit.DAYS.between(expectedReturnDate, LocalDate.now());
        } else {
            return ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate);
        }
    }

    public double calculateLateFee(double dailyRate) {
        return getDaysOverdue() * dailyRate;
    }
}