package com.port.tracking.user.dto;

import com.port.tracking.user.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private UserRole role;
}