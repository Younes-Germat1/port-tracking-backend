package com.port.tracking.fiche.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFicheRequest {

    @NotNull
    private Long importateurId;
}