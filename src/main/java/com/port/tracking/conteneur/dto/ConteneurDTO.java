package com.port.tracking.conteneur.dto;

import com.port.tracking.conteneur.ConteneurStatut;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConteneurDTO {
    private Long id;
    private Long ficheId;
    private ConteneurStatut statut;
    private String zone;
    private String rangee;
    private String position;
    private String quai;
    private LocalDateTime arrivedAt;
    private Long dwellTimeHours;
}