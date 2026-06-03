package com.port.tracking.inspection.dto;

import com.port.tracking.inspection.InspectionResultat;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionDTO {
    private Long id;
    private Long conteneurId;
    private Long inspecteurId;
    private String inspecteurNom;
    private String organisme;
    private InspectionResultat resultat;
    private LocalDateTime date;
    private String commentaire;
}