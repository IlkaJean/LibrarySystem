package com.library.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto {

    private Long id;

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "Start date and time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDateTime;

    @Positive(message = "Attendee count must be positive")
    private Integer attendeeCount;

    private String eventType; // 'E' for Exhibition, 'S' for Seminar

    // For exhibitions
    private Double expenses;

    // For seminars
    private Integer estimatedAuthors;

    private String status; // active, upcoming, past
    private int actualAttendanceCount;
}