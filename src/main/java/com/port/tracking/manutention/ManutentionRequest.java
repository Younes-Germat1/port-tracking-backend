package com.port.tracking.manutention;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManutentionRequest {
    private ManutentionType type;
    private LocalDate datePrevue;
    private LocalTime heurePrevue;
}