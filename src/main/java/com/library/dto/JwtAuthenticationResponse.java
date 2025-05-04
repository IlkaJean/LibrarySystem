package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private Long customerId;
    private Long employeeId;

    public JwtAuthenticationResponse(String accessToken, Long userId, String username,
                                     String email, List<String> roles, Long customerId, Long employeeId) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.customerId = customerId;
        this.employeeId = employeeId;
    }
}