package com.library.service;

import com.library.model.Event;
import com.library.model.Exhibition;
import com.library.model.Seminar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    Event createEvent(Event event);
    Optional<Event> getEventById(Long id);
    List<Event> getAllEvents();
    Event updateEvent(Long id, Event event);
    void deleteEvent(Long id);


    Map<Object, Object> getUpcomingEvents();

    Page<Event> getAllEvents(Pageable pageable);

    Page<Event> getEventsByType(String eventType, Pageable pageable);

    Page<Event> searchEvents(String keyword, Pageable pageable);

    List<Event> getActiveEvents();

    Page<Event> getPastEvents(Pageable pageable);

    Page<Exhibition> getAllExhibitions(Pageable pageable);

    Page<Seminar> getAllSeminars(Pageable pageable);

    @Transactional
    void registerForExhibition(Long exhibitionId, Long customerId);

    @Transactional
    void inviteAuthorToSeminar(Long seminarId, Long authorId);

    @Transactional
    void addSponsorToSeminar(Long seminarId, Long sponsorId, Double amount);

    Optional<Seminar> getSeminarById(Long id);

    Optional<Exhibition> getExhibitionById(Long id);
}