package com.port.tracking.fiche.dto;

import com.port.tracking.fiche.FicheStatut;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFicheStatutRequest {

    @NotNull
    private FicheStatut statut;

    // ✅ Optional motif for rejection
    private String motif;
}