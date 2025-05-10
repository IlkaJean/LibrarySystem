package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SponsorDto {

    private Long sponsorId;

    @NotBlank(message = "Sponsor type is required")
    private String sponsorType; // 'I' or 'O'

    private String fname;

    private String lname;

    private String orgName;
}