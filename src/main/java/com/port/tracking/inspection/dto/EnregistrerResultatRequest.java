package com.port.tracking.inspection.dto;

import com.port.tracking.inspection.InspectionResultat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnregistrerResultatRequest {

    @NotNull
    private InspectionResultat resultat;

    private String commentaire;
    private String organisme;
}