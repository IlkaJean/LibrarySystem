package com.library.service;

import com.library.model.BookCopy;
import com.library.model.Customer;
import com.library.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalService {

    Rental createRental(Customer customer, BookCopy bookCopy);

    Rental createRental(Long customerId, Long bookCopyId);

    Rental returnRental(Long rentalId);

    Rental markRentalAsLost(Long rentalId);

    List<Rental> getOverdueRentals();

    List<Rental> getRentalsDueForReturn(LocalDate startDate, LocalDate endDate);

    Optional<Rental> getRentalById(Long id);

    Page<Rental> getAllRentals(Pageable pageable);

    Page<Rental> getRentalsByCustomer(Long customerId, Pageable pageable);

    Page<Rental> getRentalsByBook(Long bookId, Pageable pageable);

    List<Rental> getActiveRentals();

    List<Rental> getCustomerActiveRentals(Long customerId);

    boolean isBookCopyAvailable(Long bookCopyId);

    boolean hasCustomerReachedRentalLimit(Long customerId);

    int getCustomerActiveRentalCount(Long customerId);

    double calculateLateFee(Rental rental);

    List<Object[]> getRentalStatsByMonth(int year);

    void processOverdueRentals();

    void generateRentalInvoice(Rental rental);
}