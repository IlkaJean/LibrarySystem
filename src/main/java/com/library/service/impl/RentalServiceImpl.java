package com.library.service.impl;

import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.BookCopy;
import com.library.model.Customer;
import com.library.model.Invoice;
import com.library.model.Rental;
import com.library.repository.BookCopyRepository;
import com.library.repository.CustomerRepository;
import com.library.repository.InvoiceRepository;
import com.library.repository.RentalRepository;
import com.library.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private static final int MAX_RENTALS_PER_CUSTOMER = 5;
    private static final double DAILY_LATE_FEE = 0.50;
    private static final int DEFAULT_RENTAL_PERIOD_DAYS = 14;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Rental createRental(Customer customer, BookCopy bookCopy) {
        // Check if customer has reached rental limit
        if (hasCustomerReachedRentalLimit(customer.getId())) {
            throw new BadRequestException("Customer has reached maximum rental limit");
        }

        // Check if book copy is available
        if (!isBookCopyAvailable(bookCopy.getId())) {
            throw new BadRequestException("Book copy is not available for rental");
        }

        // Create new rental
        Rental rental = new Rental();
        rental.setCustomer(customer);
        rental.setBookCopy(bookCopy);
        rental.setStatus("Borrowed");
        rental.setBorrowDate(LocalDate.now());
        rental.setExpectedReturnDate(LocalDate.now().plusDays(DEFAULT_RENTAL_PERIOD_DAYS));

        // Update book copy status
        bookCopy.setStatus("borrowed");
        bookCopyRepository.save(bookCopy);

        return rentalRepository.save(rental);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Rental createRental(Long customerId, Long bookCopyId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));

        return createRental(customer, bookCopy);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Rental returnRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", rentalId));

        if (!"Borrowed".equals(rental.getStatus())) {
            throw new BadRequestException("Rental is not in borrowed status");
        }

        // Update rental status and return date
        rental.setStatus("Returned");
        rental.setActualReturnDate(LocalDate.now());

        // Update book copy status
        BookCopy bookCopy = rental.getBookCopy();
        bookCopy.setStatus("available");
        bookCopyRepository.save(bookCopy);

        // Generate invoice if there are late fees
        generateRentalInvoice(rental);

        return rentalRepository.save(rental);
    }

    @Override
    @Transactional
    public Rental markRentalAsLost(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", rentalId));

        if (!"Borrowed".equals(rental.getStatus())) {
            throw new BadRequestException("Rental is not in borrowed status");
        }

        // Update rental status
        rental.setStatus("Lost");
        rental.setActualReturnDate(LocalDate.now());

        // Update book copy status
        BookCopy bookCopy = rental.getBookCopy();
        bookCopy.setStatus("lost");
        bookCopyRepository.save(bookCopy);

        // Generate invoice for lost book
        generateRentalInvoice(rental);

        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> getOverdueRentals() {
        return rentalRepository.findOverdueRentals(LocalDate.now());
    }

    @Override
    public List<Rental> getRentalsDueForReturn(LocalDate startDate, LocalDate endDate) {
        return rentalRepository.findRentalsDueForReturn(startDate, endDate);
    }

    @Override
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    @Override
    public Page<Rental> getAllRentals(Pageable pageable) {
        return rentalRepository.findAll(pageable);
    }

    @Override
    public Page<Rental> getRentalsByCustomer(Long customerId, Pageable pageable) {
        return rentalRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Rental> getRentalsByBook(Long bookId, Pageable pageable) {
        return rentalRepository.findByBookCopyBookId(bookId, pageable);
    }

    @Override
    public List<Rental> getActiveRentals() {
        return rentalRepository.findByStatus("Borrowed");
    }

    @Override
    public List<Rental> getCustomerActiveRentals(Long customerId) {
        return (List<Rental>) rentalRepository.findByCustomerIdAndStatus(customerId, "Borrowed");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookCopyAvailable(Long bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", bookCopyId));

        return "available".equals(bookCopy.getStatus());
    }

    @Override
    public boolean hasCustomerReachedRentalLimit(Long customerId) {
        int activeRentals = getCustomerActiveRentalCount(customerId);
        return activeRentals >= MAX_RENTALS_PER_CUSTOMER;
    }

    @Override
    public int getCustomerActiveRentalCount(Long customerId) {
        return getCustomerActiveRentals(customerId).size();
    }

    @Override
    public double calculateLateFee(Rental rental) {
        return rental.calculateLateFee(DAILY_LATE_FEE);
    }

    @Override
    public List<Object[]> getRentalStatsByMonth(int year) {
        return rentalRepository.countRentalsByMonth(year);
    }

    @Override
    @Transactional
    public void processOverdueRentals() {
        List<Rental> overdueRentals = getOverdueRentals();

        for (Rental rental : overdueRentals) {
            if ("Borrowed".equals(rental.getStatus())) {
                // Update status to late if it's overdue but still borrowed
                rental.setStatus("Late");
                rentalRepository.save(rental);
            }
        }
    }

    @Override
    @Transactional
    public void generateRentalInvoice(Rental rental) {
        // Only generate invoice if there are late fees or book is lost
        double lateFee = calculateLateFee(rental);
        double totalAmount = lateFee;

        if ("Lost".equals(rental.getStatus())) {
            // Add book replacement cost (simplified to fixed amount)
            totalAmount += 50.00;
        }

        if (totalAmount > 0) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceDate(LocalDate.now());
            invoice.setAmount(totalAmount);
            invoice.setRental(rental);

            invoiceRepository.save(invoice);

            rental.setInvoice(invoice);
            rentalRepository.save(rental);
        }
    }
}