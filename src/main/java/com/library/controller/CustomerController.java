package com.library.controller;

import com.library.dto.CustomerDto;
import com.library.model.Customer;
import com.library.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<CustomerDto> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerService.getAllCustomers(pageable);

        return customers.map(this::convertToDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or (#id == authentication.principal.customerId)")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        Customer customer = convertToEntity(customerDto);
        Customer createdCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(convertToDto(createdCustomer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or (#id == authentication.principal.customerId)")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDto customerDto) {
        Customer customer = convertToEntity(customerDto);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(convertToDto(updatedCustomer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<CustomerDto> searchCustomers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerService.searchCustomers(keyword, pageable);

        return customers.map(this::convertToDto);
    }

    @GetMapping("/search/email")
    public ResponseEntity<CustomerDto> findByEmail(@RequestParam String email) {
        return customerService.findByEmail(email)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/phone")
    public ResponseEntity<CustomerDto> findByPhone(@RequestParam String phone) {
        return customerService.findByPhone(phone)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active-rentals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<CustomerDto> getCustomersWithActiveRentals() {
        List<Customer> customers = customerService.getCustomersWithActiveRentals();
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/overdue-rentals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<CustomerDto> getCustomersWithOverdueRentals() {
        List<Customer> customers = customerService.getCustomersWithOverdueRentals();
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/top-renters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<CustomerDto> getTopCustomersByRentals(@RequestParam(defaultValue = "10") int limit) {
        List<Customer> customers = customerService.getTopCustomersByRentals(limit);
        return customers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        BeanUtils.copyProperties(customer, dto);

        dto.setActiveRentals((int) customer.getActiveRentalCount());
        dto.setTotalRentals(customer.getRentals().size());

        return dto;
    }

    private Customer convertToEntity(CustomerDto dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        return customer;
    }
}