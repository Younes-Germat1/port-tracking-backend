package com.port.tracking.fiche.dto;

import com.port.tracking.fiche.FicheStatut;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheDTO {
    private Long id;
    private Long importateurId;
    private String importateurNom;
    private FicheStatut statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}