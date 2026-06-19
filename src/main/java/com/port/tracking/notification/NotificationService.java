package com.port.tracking.notification;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(User user, FicheSuiveuse fiche, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .fiche(fiche)
                .message(message != null && !message.isBlank() ? message : "🔔 Statut de votre fiche mis à jour.")
                .lu(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<NotificationDTO> getMyNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUserAndLu(user, false)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setLu(true);
        return toDTO(notificationRepository.save(notification));
    }

    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepository.findByUserAndLu(user, false);
        unread.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .message(n.getMessage() != null ? n.getMessage() : "🔔 Notification")
                .lu(n.getLu())
                .createdAt(n.getCreatedAt())
                .ficheId(n.getFiche() != null ? n.getFiche().getId() : null)
                .build();
    }
}