package com.port.tracking.fiche.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResoumissionRequest {

    @NotBlank
    private String importateurNom;

    @NotBlank
    private String importateurAdresse;

    @NotBlank
    private String importateurContact;
}