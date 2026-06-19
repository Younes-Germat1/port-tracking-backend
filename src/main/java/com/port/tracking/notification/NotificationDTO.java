package com.port.tracking.notification;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String message;
    private Boolean lu;
    private LocalDateTime createdAt;
    private Long ficheId;
}