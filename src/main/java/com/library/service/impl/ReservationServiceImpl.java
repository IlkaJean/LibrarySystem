package com.library.service.impl;

import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Customer;
import com.library.model.Reservation;
import com.library.model.StudyRoom;
import com.library.repository.CustomerRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.StudyRoomRepository;
import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Validate reservation times
        if (reservation.getEndDateTime().isBefore(reservation.getStartDateTime())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Check if study room is available
        if (!isStudyRoomAvailable(reservation.getStudyRoom().getId(),
                reservation.getStartDateTime(), reservation.getEndDateTime())) {
            throw new BadRequestException("Study room is not available for the selected time");
        }

        // Validate group size against room capacity
        StudyRoom room = studyRoomRepository.findById(reservation.getStudyRoom().getId())
                .orElseThrow(() -> new ResourceNotFoundException("StudyRoom", "id", reservation.getStudyRoom().getId()));

        if (reservation.getGroupSize() > room.getCapacity()) {
            throw new BadRequestException("Group size exceeds room capacity");
        }

        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    @Override
    public Page<Reservation> getReservationsByCustomer(Long customerId, Pageable pageable) {
        return reservationRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    public Page<Reservation> getReservationsByStudyRoom(Long studyRoomId, Pageable pageable) {
        return reservationRepository.findByStudyRoomId(studyRoomId, pageable);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservation.setTopicDescription(reservationDetails.getTopicDescription());
        reservation.setStartDateTime(reservationDetails.getStartDateTime());
        reservation.setEndDateTime(reservationDetails.getEndDateTime());
        reservation.setGroupSize(reservationDetails.getGroupSize());

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservationRepository.delete(reservation);
    }

    @Override
    public List<Reservation> getActiveReservations() {
        return reservationRepository.findActiveReservations(LocalDateTime.now());
    }

    @Override
    public List<Reservation> getUpcomingReservations() {
        return reservationRepository.findUpcomingReservations(LocalDateTime.now());
    }

    @Override
    public List<Long> searchAvailableStudyRooms(LocalDateTime start, LocalDateTime end, int groupSize) {
        List<StudyRoom> availableRooms = studyRoomRepository.findAvailableRooms(start, end, groupSize);
        return availableRooms.stream()
                .map(StudyRoom::getId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isStudyRoomAvailable(Long studyRoomId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                studyRoomId, start, end);
        return overlappingReservations.isEmpty();
    }

    @Override
    @Transactional
    public Reservation quickBookStudyRoom(Long customerId, String topicDescription,
                                          LocalDateTime start, LocalDateTime end, int groupSize) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<Long> availableRoomIds = searchAvailableStudyRooms(start, end, groupSize);
        if (availableRoomIds.isEmpty()) {
            throw new BadRequestException("No study rooms available for the selected time and size");
        }

        StudyRoom room = studyRoomRepository.findById(availableRoomIds.get(0))
                .orElseThrow(() -> new ResourceNotFoundException("StudyRoom", "id", availableRoomIds.get(0)));

        Reservation reservation = new Reservation();
        reservation.setCustomer(customer);
        reservation.setStudyRoom(room);
        reservation.setTopicDescription(topicDescription);
        reservation.setStartDateTime(start);
        reservation.setEndDateTime(end);
        reservation.setGroupSize(groupSize);

        return createReservation(reservation);
    }

    @Override
    @Transactional
    public void cleanupOldReservations() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        reservationRepository.deleteOldReservations(oneYearAgo);
    }
}