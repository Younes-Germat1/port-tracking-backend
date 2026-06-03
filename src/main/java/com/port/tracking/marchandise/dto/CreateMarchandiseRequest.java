package com.port.tracking.marchandise.dto;

import com.port.tracking.marchandise.ClassificationMarchandise;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarchandiseRequest {

    @NotNull
    private Long ficheId;

    @NotNull
    private ClassificationMarchandise classification;

    private Double poids;
    private Double volume;
    private String codeSh;
}