package com.port.tracking.marchandise.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarchandiseDTO {
    private Long id;
    private Long ficheId;
    private String description;
    private String classification;
    private Double poids;
    private Double volume;
    private String codeSh;
}