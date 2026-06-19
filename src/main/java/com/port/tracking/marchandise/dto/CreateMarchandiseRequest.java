package com.port.tracking.marchandise.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarchandiseRequest {

    @NotNull
    private Long ficheId;

    private String description;
    private String classification;
    private Double poids;
    private Double volume;
    private String codeSh;
}