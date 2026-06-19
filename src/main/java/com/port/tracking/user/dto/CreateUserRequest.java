package com.port.tracking.user.dto;

import com.port.tracking.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String nom;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String telephone;

    @NotNull
    private UserRole role;
}