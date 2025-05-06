package com.library.service.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.*;
import com.library.repository.*;
import com.library.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private SeminarRepository seminarRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Override
    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Override
    public Page<Event> getEventsByType(String eventType, Pageable pageable) {
        return eventRepository.findByEventType(eventType, pageable);
    }

    @Override
    public Page<Event> searchEvents(String keyword, Pageable pageable) {
        return eventRepository.searchEvents(keyword, pageable);
    }

    @Override
    @Transactional
    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        event.setName(eventDetails.getName());
        event.setStartDateTime(eventDetails.getStartDateTime());
        event.setEndDateTime(eventDetails.getEndDateTime());
        event.setAttendeeCount(eventDetails.getAttendeeCount());

        return eventRepository.save(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));

        eventRepository.delete(event);
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    @Override
    public List<Event> getActiveEvents() {
        return eventRepository.findActiveEvents(LocalDateTime.now());
    }

    @Override
    public Page<Event> getPastEvents(Pageable pageable) {
        return eventRepository.findPastEvents(LocalDateTime.now(), pageable);
    }

    @Override
    public Page<Exhibition> getAllExhibitions(Pageable pageable) {
        return exhibitionRepository.findAll(pageable);
    }

    @Override
    public Page<Seminar> getAllSeminars(Pageable pageable) {
        return seminarRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void registerForExhibition(Long exhibitionId, Long customerId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exhibition", "id", exhibitionId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        ExhibitionAttendance attendance = new ExhibitionAttendance();
        attendance.setExhibition(exhibition);
        attendance.setCustomer(customer);

        exhibition.getAttendances().add(attendance);
        exhibitionRepository.save(exhibition);
    }

    @Override
    @Transactional
    public void inviteAuthorToSeminar(Long seminarId, Long authorId) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar", "id", seminarId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));

        SeminarAttendance attendance = new SeminarAttendance();
        attendance.setSeminar(seminar);
        attendance.setAuthor(author);

        seminar.getAttendances().add(attendance);
        seminarRepository.save(seminar);
    }

    @Override
    @Transactional
    public void addSponsorToSeminar(Long seminarId, Long sponsorId, Double amount) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar", "id", seminarId));

        Sponsor sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor", "id", sponsorId));

        SeminarSponsor seminarSponsor = new SeminarSponsor();
        seminarSponsor.setSeminar(seminar);
        seminarSponsor.setSponsor(sponsor);
        seminarSponsor.setAmount(amount);

        seminar.getSponsors().add(seminarSponsor);
        seminarRepository.save(seminar);
    }

    @Override
    public Optional<Seminar> getSeminarById(Long id) {
        return seminarRepository.findById(id);
    }

    @Override
    public Optional<Exhibition> getExhibitionById(Long id) {
        return exhibitionRepository.findById(id);
    }
}