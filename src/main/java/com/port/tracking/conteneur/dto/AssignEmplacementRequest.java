package com.port.tracking.conteneur.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignEmplacementRequest {
    private String zone;
    private String rangee;
    private String position;
    private String quai;
}