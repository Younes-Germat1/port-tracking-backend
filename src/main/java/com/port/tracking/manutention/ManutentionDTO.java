package com.port.tracking.manutention;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManutentionDTO {
    private Long id;
    private Long conteneurId;
    private ManutentionType type;
    private LocalDate datePrevue;
    private LocalTime heurePrevue;
    private LocalDateTime createdAt;
}