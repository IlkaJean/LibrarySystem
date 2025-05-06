package com.library.service.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Invoice;
import com.library.model.Payment;
import com.library.model.Rental;
import com.library.repository.InvoiceRepository;
import com.library.repository.PaymentRepository;
import com.library.repository.RentalRepository;
import com.library.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Invoice> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Invoice> getUnpaidInvoices() {
        return invoiceRepository.findUnpaidInvoices();
    }

    @Override
    public Double getTotalInvoiceAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.getTotalInvoiceAmountByDateRange(startDate, endDate);
    }

    @Override
    @Transactional
    public Payment makePayment(Long invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        payment.setInvoice(invoice);
        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice payments
        invoice.getPayments().add(savedPayment);
        invoiceRepository.save(invoice);

        return savedPayment;
    }

    @Override
    public boolean isInvoicePaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        return invoice.isPaid();
    }

    @Override
    public Double getInvoiceBalance(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        return invoice.getBalance();
    }

    @Override
    @Transactional
    public void generateInvoiceForRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", rentalId));

        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setAmount(calculateRentalAmount(rental));
        invoice.setRental(rental);

        invoiceRepository.save(invoice);

        rental.setInvoice(invoice);
        rentalRepository.save(rental);
    }

    private Double calculateRentalAmount(Rental rental) {
        double amount = 0.0;

        if (rental.isOverdue()) {
            amount += rental.getDaysOverdue() * 0.50; // $0.50 per day late fee
        }

        if ("Lost".equals(rental.getStatus())) {
            amount += 50.00; // Fixed amount for lost book
        }

        return amount;
    }
}