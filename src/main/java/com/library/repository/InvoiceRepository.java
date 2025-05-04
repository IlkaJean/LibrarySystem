package com.library.repository;

import com.library.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByRentalId(Long rentalId);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE i.isPaid() = false")
    List<Invoice> findUnpaidInvoices();

    @Query("SELECT SUM(i.amount) FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    Double getTotalInvoiceAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i JOIN i.rental r WHERE r.customer.id = :customerId")
    List<Invoice> findByCustomerId(@Param("customerId") Long customerId);
}