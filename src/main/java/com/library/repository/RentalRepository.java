package com.library.repository;

import com.library.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Page<Rental> findByCustomerId(Long customerId, Pageable pageable);

    Page<Rental> findByCustomerIdAndStatus(Long customerId, String status, Pageable pageable);

    Page<Rental> findByBookCopyBookId(Long bookId, Pageable pageable);

    List<Rental> findByStatus(String status);

    @Query("SELECT r FROM Rental r WHERE r.status = 'Borrowed' AND r.expectedReturnDate < :currentDate")
    List<Rental> findOverdueRentals(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT r FROM Rental r WHERE r.status = 'Borrowed' AND r.expectedReturnDate BETWEEN :startDate AND :endDate")
    List<Rental> findRentalsDueForReturn(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Rental r WHERE r.bookCopy.id = :copyId AND r.status = 'Borrowed'")
    List<Rental> findActiveRentalsByBookCopy(@Param("copyId") Long copyId);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.bookCopy.book.id = :bookId AND r.status = 'Borrowed'")
    long countActiveRentalsByBook(@Param("bookId") Long bookId);

    @Query("SELECT MONTH(r.borrowDate), COUNT(r) FROM Rental r " +
            "WHERE YEAR(r.borrowDate) = :year " +
            "GROUP BY MONTH(r.borrowDate) " +
            "ORDER BY MONTH(r.borrowDate)")
    List<Object[]> countRentalsByMonth(@Param("year") int year);

    @Query(value = "SELECT * FROM PJI_RENTAL r " +
            "WHERE r.STATUS = 'Borrowed' " +
            "AND r.PJI_CUSTOMER_CUST_ID = :customerId " +
            "FOR UPDATE WAIT 5",
            nativeQuery = true)
    List<Rental> findActiveRentalsWithLock(@Param("customerId") Long customerId);
}