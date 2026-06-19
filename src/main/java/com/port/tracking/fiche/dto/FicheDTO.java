package com.port.tracking.fiche.dto;

import com.port.tracking.fiche.FicheStatut;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheDTO {
    private Long id;
    private Long importateurId;
    private String importateurNom;
    private String importateurAdresse;
    private String importateurContact;
    private FicheStatut statut;
    private List<MarchandiseDTO> marchandises;
    private List<String> organismes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarchandiseDTO {
        private Long id;
        private String description;
        private String classification;
        private String codeSH;
        private Double poids;
        private Double volume;
    }
}