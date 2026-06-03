package com.port.tracking.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/{id}/lu")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}