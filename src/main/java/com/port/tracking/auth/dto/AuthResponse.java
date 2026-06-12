package com.port.tracking.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String token;
    private String role;
    private String email;
    private String nom;
}