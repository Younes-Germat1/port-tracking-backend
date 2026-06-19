package com.port.tracking.fiche.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFicheRequest {

    @NotNull
    private Long importateurId;

    @NotBlank
    private String importateurNom;

    @NotBlank
    private String importateurAdresse;

    @NotBlank
    private String importateurContact;

    private List<MarchandiseRequest> marchandises;
    private List<String> organismes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarchandiseRequest {
        private String description;
        private String classification;
        private String codeSH;
        private Double poids;
        private Double volume;
    }
}