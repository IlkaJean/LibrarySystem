package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDto {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9-]+$", message = "Phone must contain only digits and hyphens")
    @Size(max = 15, message = "Phone must not exceed 15 characters")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "ID type is required")
    @Pattern(regexp = "^(Passport|SSN|Driver License)$", message = "ID type must be Passport, SSN, or Driver License")
    private String idType;

    @NotBlank(message = "ID number is required")
    @Size(max = 30, message = "ID number must not exceed 30 characters")
    private String idNumber;

    private int activeRentals;
    private int totalRentals;
}