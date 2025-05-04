package com.library.repository;

import com.library.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByCustomerId(Long customerId, Pageable pageable);

    Page<Reservation> findByStudyRoomId(Long studyRoomId, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.startDateTime <= :currentDateTime AND r.endDateTime >= :currentDateTime")
    List<Reservation> findActiveReservations(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT r FROM Reservation r WHERE r.startDateTime > :currentDateTime")
    List<Reservation> findUpcomingReservations(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT r FROM Reservation r WHERE r.endDateTime < :currentDateTime")
    Page<Reservation> findPastReservations(@Param("currentDateTime") LocalDateTime currentDateTime, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.studyRoom.id = :studyRoomId AND " +
            "NOT (r.endDateTime <= :startDateTime OR r.startDateTime >= :endDateTime)")
    List<Reservation> findOverlappingReservations(
            @Param("studyRoomId") Long studyRoomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT DISTINCT r.studyRoom.id FROM Reservation r WHERE " +
            "NOT (r.endDateTime <= :startDateTime OR r.startDateTime >= :endDateTime)")
    List<Long> findOccupiedStudyRoomIds(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("DELETE FROM Reservation r WHERE r.endDateTime < :dateTime")
    void deleteOldReservations(@Param("dateTime") LocalDateTime dateTime);
}