package com.library.controller;

import com.library.dto.ReservationDto;
import com.library.model.Reservation;
import com.library.security.UserPrincipal;
import com.library.service.ReservationService;
import jakarta.validation.Valid;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<ReservationDto> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationService.getAllReservations(pageable);

        return reservations.map(this::convertToDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or " +
            "(hasRole('CUSTOMER') and @reservationService.getReservationById(#id).orElse(null)?.customer?.id == authentication.principal.customerId)")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CUSTOMER')")
    public ResponseEntity<ReservationDto> createReservation(
            @Valid @RequestBody ReservationDto reservationDto,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        // If user is a customer, set the customer ID to their own ID
        if (currentUser.isCustomer() && !currentUser.isAdmin() && !currentUser.isEmployee()) {
            reservationDto.setCustomerId(currentUser.getCustomerId());
        }

        Reservation reservation = convertToEntity(reservationDto);
        Reservation createdReservation = reservationService.createReservation(reservation);
        return new ResponseEntity<>(convertToDto(createdReservation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or " +
            "(hasRole('CUSTOMER') and @reservationService.getReservationById(#id).orElse(null)?.customer?.id == authentication.principal.customerId)")
    public ResponseEntity<ReservationDto> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto) {
        Reservation reservation = convertToEntity(reservationDto);
        Reservation updatedReservation = reservationService.updateReservation(id, reservation);
        return ResponseEntity.ok(convertToDto(updatedReservation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or " +
            "(hasRole('CUSTOMER') and @reservationService.getReservationById(#id).orElse(null)?.customer?.id == authentication.principal.customerId)")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or (#customerId == authentication.principal.customerId)")
    public Page<ReservationDto> getReservationsByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationService.getReservationsByCustomer(customerId, pageable);

        return reservations.map(this::convertToDto);
    }

    @GetMapping("/room/{studyRoomId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<ReservationDto> getReservationsByStudyRoom(
            @PathVariable Long studyRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationService.getReservationsByStudyRoom(studyRoomId, pageable);

        return reservations.map(this::convertToDto);
    }

    @GetMapping("/active")
    public List<ReservationDto> getActiveReservations() {
        List<Reservation> reservations = reservationService.getActiveReservations();
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/upcoming")
    public List<ReservationDto> getUpcomingReservations() {
        List<Reservation> reservations = reservationService.getUpcomingReservations();
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search-available-rooms")
    public List<Long> searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam int groupSize) {
        return reservationService.searchAvailableStudyRooms(start, end, groupSize);
    }

    @GetMapping("/check-room-availability")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam Long studyRoomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        boolean isAvailable = reservationService.isStudyRoomAvailable(studyRoomId, start, end);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<ReservationDto> getMyReservations(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (currentUser.getCustomerId() == null) {
            throw new IllegalStateException("User is not associated with a customer");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationService.getReservationsByCustomer(currentUser.getCustomerId(), pageable);

        return reservations.map(this::convertToDto);
    }

    @PostMapping("/quick-create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReservationDto> quickCreateReservation(
            @RequestParam String topicDescription,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam int durationInHours,
            @RequestParam int groupSize,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LocalDateTime endDateTime = startDateTime.plusHours(durationInHours);

        Reservation reservation = reservationService.quickBookStudyRoom(
                currentUser.getCustomerId(),
                topicDescription,
                startDateTime,
                endDateTime,
                groupSize
        );

        return new ResponseEntity<>(convertToDto(reservation), HttpStatus.CREATED);
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        BeanUtils.copyProperties(reservation, dto);

        if (reservation.getCustomer() != null) {
            dto.setCustomerId(reservation.getCustomer().getId());
            dto.setCustomerName(reservation.getCustomer().getFullName());
        }

        if (reservation.getStudyRoom() != null) {
            dto.setStudyRoomId(reservation.getStudyRoom().getId());
            dto.setRoomCapacity(reservation.getStudyRoom().getCapacity());
        }

        dto.setStatus(reservation.getStatus());

        return dto;
    }

    private Reservation convertToEntity(ReservationDto dto) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(dto, reservation);
        return reservation;
    }
}