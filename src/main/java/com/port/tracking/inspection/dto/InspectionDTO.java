package com.port.tracking.inspection.dto;

import com.port.tracking.inspection.InspectionResultat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private Long dwellTimeHours;
    private String photoPath;
    private List<MarchandiseInfo> marchandises;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarchandiseInfo {
        private Long id;
        private String description;
        private String classification;
        private String codeSH;
        private Double poids;
        private Double volume;
    }
}