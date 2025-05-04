package com.library.repository;

import com.library.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByEventType(String eventType, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Event> searchEvents(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDateTime < :currentDateTime")
    Page<Event> findPastEvents(@Param("currentDateTime") LocalDateTime currentDateTime, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.startDateTime > :currentDateTime")
    List<Event> findUpcomingEvents(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT e FROM Event e WHERE e.startDateTime <= :currentDateTime AND e.endDateTime >= :currentDateTime")
    List<Event> findActiveEvents(@Param("currentDateTime") LocalDateTime currentDateTime);

    @Query("SELECT e FROM Event e WHERE e.eventType = 'E'")
    Page<Event> findAllExhibitions(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.eventType = 'S'")
    Page<Event> findAllSeminars(Pageable pageable);
}