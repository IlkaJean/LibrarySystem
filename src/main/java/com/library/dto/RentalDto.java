package com.library.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RentalDto {

    private Long id;
    private String status;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;

    private Long customerId;
    private String customerName;

    private Long bookCopyId;
    private Long bookId;
    private String bookName;
    private String bookAuthors;

    private boolean overdue;
    private long daysOverdue;
    private double lateFee;

    private Long invoiceId;
    private Double invoiceAmount;
}