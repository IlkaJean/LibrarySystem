package com.library.service;

import com.library.model.Invoice;
import com.library.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Optional<Invoice> getInvoiceById(Long id);
    Page<Invoice> getAllInvoices(Pageable pageable); // Instead of List

    Invoice updateInvoice(Long id, Invoice invoice);
    void deleteInvoice(Long id);

    List<Invoice> getInvoicesByCustomer(Long customerId);

    List<Invoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate);

    List<Invoice> getUnpaidInvoices();

    Double getTotalInvoiceAmountByDateRange(LocalDate startDate, LocalDate endDate);

    @Transactional
    Payment makePayment(Long invoiceId, Payment payment);

    boolean isInvoicePaid(Long invoiceId);

    Double getInvoiceBalance(Long invoiceId);

    @Transactional
    void generateInvoiceForRental(Long rentalId);
}