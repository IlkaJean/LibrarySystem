package com.library.service;

import com.library.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> getReservationById(Long id);
    List<Reservation> getAllReservations(Pageable pageable);
    Reservation updateReservation(Long id, Reservation reservationDetails);
    void deleteReservation(Long id);


    Map<Object, Object> getActiveReservations();

    void cleanupOldReservations();

    Page<Reservation> getReservationsByCustomer(Long customerId, Pageable pageable);

    Reservation quickBookStudyRoom(Long customerId, String topicDescription, LocalDateTime startDateTime, LocalDateTime endDateTime, int groupSize);

    boolean isStudyRoomAvailable(Long studyRoomId, LocalDateTime start, LocalDateTime end);

    Page<Reservation> getReservationsByStudyRoom(Long studyRoomId, Pageable pageable);

    List<Reservation> getUpcomingReservations();

    List<Long> searchAvailableStudyRooms(LocalDateTime start, LocalDateTime end, int groupSize);
}
