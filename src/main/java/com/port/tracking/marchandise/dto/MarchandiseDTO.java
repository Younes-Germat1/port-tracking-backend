package com.port.tracking.marchandise.dto;

import com.port.tracking.marchandise.ClassificationMarchandise;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarchandiseDTO {
    private Long id;
    private Long ficheId;
    private ClassificationMarchandise classification;
    private Double poids;
    private Double volume;
    private String codeSh;
}