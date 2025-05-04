package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {

    private Long id;

    @NotBlank(message = "Topic description is required")
    private String topicDescription;

    @NotNull(message = "Start date and time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time is required")
    private LocalDateTime endDateTime;

    @Positive(message = "Group size must be positive")
    private Integer groupSize;

    private Long customerId;
    private String customerName;

    private Long studyRoomId;
    private Integer roomCapacity;

    private String status; // active, upcoming, completed
}