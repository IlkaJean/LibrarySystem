package com.library.controller;

import com.library.dto.RentalDto;
import com.library.model.Rental;
import com.library.security.UserPrincipal;
import com.library.service.RentalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<RentalDto> getAllRentals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rental> rentals = rentalService.getAllRentals(pageable);

        return rentals.map(this::convertToDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or " +
            "(hasRole('CUSTOMER') and @rentalService.getRentalById(#id).orElse(null)?.customer?.id == authentication.principal.customerId)")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<RentalDto> createRental(
            @RequestParam Long customerId,
            @RequestParam Long bookCopyId) {
        Rental rental = rentalService.createRental(customerId, bookCopyId);
        return new ResponseEntity<>(convertToDto(rental), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<RentalDto> returnRental(@PathVariable Long id) {
        Rental rental = rentalService.returnRental(id);
        return ResponseEntity.ok(convertToDto(rental));
    }

    @PostMapping("/{id}/mark-lost")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<RentalDto> markRentalAsLost(@PathVariable Long id) {
        Rental rental = rentalService.markRentalAsLost(id);
        return ResponseEntity.ok(convertToDto(rental));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or (#customerId == authentication.principal.customerId)")
    public Page<RentalDto> getRentalsByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rental> rentals = rentalService.getRentalsByCustomer(customerId, pageable);

        return rentals.map(this::convertToDto);
    }

    @GetMapping("/customer/{customerId}/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or (#customerId == authentication.principal.customerId)")
    public List<RentalDto> getCustomerActiveRentals(@PathVariable Long customerId) {
        List<Rental> rentals = rentalService.getCustomerActiveRentals(customerId);
        return rentals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<RentalDto> getRentalsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rental> rentals = rentalService.getRentalsByBook(bookId, pageable);

        return rentals.map(this::convertToDto);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<RentalDto> getOverdueRentals() {
        List<Rental> rentals = rentalService.getOverdueRentals();
        return rentals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/due-for-return")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<RentalDto> getRentalsDueForReturn(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Rental> rentals = rentalService.getRentalsDueForReturn(startDate, endDate);
        return rentals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<RentalDto> getActiveRentals() {
        List<Rental> rentals = rentalService.getActiveRentals();
        return rentals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/stats/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<Object[]> getRentalStatsByMonth(@RequestParam int year) {
        return rentalService.getRentalStatsByMonth(year);
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processOverdueRentals() {
        rentalService.processOverdueRentals();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-rentals")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<RentalDto> getMyRentals(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (currentUser.getCustomerId() == null) {
            throw new IllegalStateException("User is not associated with a customer");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Rental> rentals = rentalService.getRentalsByCustomer(currentUser.getCustomerId(), pageable);

        return rentals.map(this::convertToDto);
    }

    private RentalDto convertToDto(Rental rental) {
        RentalDto dto = new RentalDto();
        BeanUtils.copyProperties(rental, dto);

        if (rental.getCustomer() != null) {
            dto.setCustomerId(rental.getCustomer().getId());
            dto.setCustomerName(rental.getCustomer().getFullName());
        }

        if (rental.getBookCopy() != null) {
            dto.setBookCopyId(rental.getBookCopy().getId());

            if (rental.getBookCopy().getBook() != null) {
                dto.setBookId(rental.getBookCopy().getBook().getId());
                dto.setBookName(rental.getBookCopy().getBook().getName());
                dto.setBookAuthors(rental.getBookCopy().getBook().getAuthorNames());
            }
        }

        if (rental.getInvoice() != null) {
            dto.setInvoiceId(rental.getInvoice().getId());
            dto.setInvoiceAmount(rental.getInvoice().getAmount());
        }

        dto.setOverdue(rental.isOverdue());
        dto.setDaysOverdue(rental.getDaysOverdue());
        dto.setLateFee(rentalService.calculateLateFee(rental));

        return dto;
    }
}