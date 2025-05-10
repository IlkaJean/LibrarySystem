package com.library.service;

import com.library.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Optional<Customer> getCustomerById(Long id);
    Page<Customer> getAllCustomers(Pageable pageable);

    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);

    Optional<Customer> findByEmail(String email);

    Page<Customer> searchCustomers(String keyword, Pageable pageable);

    Optional<Customer>  findByPhone(String phone);

    List<Customer> getCustomersWithActiveRentals();

    List<Customer> getCustomersWithOverdueRentals();

    List<Customer> getTopCustomersByRentals(int limit);
}