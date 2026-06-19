package com.port.tracking.historique;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueDTO {
    private Long id;
    private String action;
    private String details;
    private LocalDateTime timestamp;
    private String acteurNom;
}