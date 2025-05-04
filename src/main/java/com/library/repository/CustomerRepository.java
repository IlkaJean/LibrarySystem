package com.library.repository;

import com.library.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    Page<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.idType = :idType AND c.idNumber = :idNumber")
    Optional<Customer> findByIdTypeAndIdNumber(
            @Param("idType") String idType,
            @Param("idNumber") String idNumber);

    @Query("SELECT c FROM Customer c JOIN c.rentals r WHERE r.status = 'Borrowed' GROUP BY c")
    List<Customer> findCustomersWithActiveRentals();

    @Query("SELECT c FROM Customer c JOIN c.rentals r WHERE r.status = 'Borrowed' AND r.isOverdue() = true GROUP BY c")
    List<Customer> findCustomersWithOverdueRentals();

    @Query("SELECT c FROM Customer c JOIN c.rentals r GROUP BY c ORDER BY COUNT(r) DESC")
    List<Customer> findTopCustomersByRentals(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR c.phone LIKE CONCAT('%', :keyword, '%')")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
}