package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class BookDto {

    private Long id;

    @NotBlank(message = "Book name is required")
    @Size(max = 100, message = "Book name must not exceed 100 characters")
    private String name;

    private Long topicId;
    private String topicName;

    @NotEmpty(message = "At least one author is required")
    private Set<Long> authorIds;

    private Set<String> authorNames;

    private int totalCopies;
    private int availableCopies;
}