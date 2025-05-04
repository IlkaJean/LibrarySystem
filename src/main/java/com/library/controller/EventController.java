package com.library.controller;

import com.library.dto.EventDto;
import com.library.model.Event;
import com.library.model.Exhibition;
import com.library.model.Seminar;
import com.library.service.EventService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public Page<EventDto> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getAllEvents(pageable);

        return events.map(this::convertToDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto eventDto) {
        Event event = convertToEntity(eventDto);
        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(convertToDto(createdEvent), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDto eventDto) {
        Event event = convertToEntity(eventDto);
        Event updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(convertToDto(updatedEvent));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{eventType}")
    public Page<EventDto> getEventsByType(
            @PathVariable String eventType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getEventsByType(eventType, pageable);

        return events.map(this::convertToDto);
    }

    @GetMapping("/exhibitions")
    public Page<EventDto> getAllExhibitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exhibition> exhibitions = eventService.getAllExhibitions(pageable);

        return exhibitions.map(this::convertToDto);
    }

    @GetMapping("/seminars")
    public Page<EventDto> getAllSeminars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seminar> seminars = eventService.getAllSeminars(pageable);

        return seminars.map(this::convertToDto);
    }

    @GetMapping("/search")
    public Page<EventDto> searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.searchEvents(keyword, pageable);

        return events.map(this::convertToDto);
    }

    @GetMapping("/upcoming")
    public List<EventDto> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/active")
    public List<EventDto> getActiveEvents() {
        List<Event> events = eventService.getActiveEvents();
        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/past")
    public Page<EventDto> getPastEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventService.getPastEvents(pageable);

        return events.map(this::convertToDto);
    }

    @PostMapping("/exhibitions/{exhibitionId}/register")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> registerForExhibition(
            @PathVariable Long exhibitionId,
            @RequestParam Long customerId) {
        eventService.registerForExhibition(exhibitionId, customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seminars/{seminarId}/invite-author")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> inviteAuthorToSeminar(
            @PathVariable Long seminarId,
            @RequestParam Long authorId) {
        eventService.inviteAuthorToSeminar(seminarId, authorId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seminars/{seminarId}/add-sponsor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> addSponsorToSeminar(
            @PathVariable Long seminarId,
            @RequestParam Long sponsorId,
            @RequestParam Double amount) {
        eventService.addSponsorToSeminar(seminarId, sponsorId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/seminars/{seminarId}/total-sponsorship")
    public ResponseEntity<Double> getTotalSponsorshipAmount(@PathVariable Long seminarId) {
        return eventService.getSeminarById(seminarId)
                .map(seminar -> seminar.getTotalSponsorshipAmount())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private EventDto convertToDto(Event event) {
        EventDto dto = new EventDto();
        BeanUtils.copyProperties(event, dto);

        // Set status based on event dates
        LocalDateTime now = LocalDateTime.now();
        if (event.isActive()) {
            dto.setStatus("active");
        } else if (event.isUpcoming()) {
            dto.setStatus("upcoming");
        } else {
            dto.setStatus("past");
        }

        // Set specific properties for exhibitions and seminars
        if (event instanceof Exhibition) {
            Exhibition exhibition = (Exhibition) event;
            dto.setExpenses(exhibition.getExpenses());
            dto.setActualAttendanceCount(exhibition.getActualAttendanceCount());
        } else if (event instanceof Seminar) {
            Seminar seminar = (Seminar) event;
            dto.setEstimatedAuthors(seminar.getEstimatedAuthors());
            dto.setActualAttendanceCount(seminar.getActualAuthorCount());
        }

        return dto;
    }

    private Event convertToEntity(EventDto dto) {
        if ("E".equals(dto.getEventType())) {
            Exhibition exhibition = new Exhibition();
            BeanUtils.copyProperties(dto, exhibition);
            exhibition.setExpenses(dto.getExpenses());
            return exhibition;
        } else if ("S".equals(dto.getEventType())) {
            Seminar seminar = new Seminar();
            BeanUtils.copyProperties(dto, seminar);
            seminar.setEstimatedAuthors(dto.getEstimatedAuthors());
            return seminar;
        } else {
            throw new IllegalArgumentException("Invalid event type: " + dto.getEventType());
        }
    }
}