package com.port.tracking.notification;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.fiche.FicheRepository;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FicheRepository ficheRepository;

    public Notification createNotification(User user, FicheSuiveuse fiche, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .fiche(fiche)
                .message(message != null && !message.isBlank() ? message : "🔔 Statut de votre fiche mis à jour.")
                .lu(false)
                .build();
        return notificationRepository.save(notification);
    }

    /**
     * ✅ Admin-triggered alert: sends a message to every user of a given role,
     * optionally linked to a specific fiche so recipients can click through to it.
     */
    public List<NotificationDTO> sendAdminAlert(AdminAlertRequest request) {
        List<User> targets = userRepository.findByRole(request.getTargetRole());

        FicheSuiveuse fiche = null;
        if (request.getFicheId() != null) {
            fiche = ficheRepository.findById(request.getFicheId())
                    .orElseThrow(() -> new RuntimeException("Fiche not found"));
        }

        String prefixedMessage = "📢 [Admin] " + request.getMessage();
        FicheSuiveuse finalFiche = fiche;

        return targets.stream()
                .map(u -> createNotification(u, finalFiche, prefixedMessage))
                .map(this::toDTO)
                .collect(Collectors.toList());
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

    public void clearNotificationsForFiche(FicheSuiveuse fiche) {
        List<Notification> unread = notificationRepository.findByFicheAndLu(fiche, false);
        unread.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(unread);
    }

    public void clearNotificationsForFicheAndRole(FicheSuiveuse fiche, List<User> usersOfRole) {
        for (User user : usersOfRole) {
            List<Notification> unread = notificationRepository.findByUserAndFicheAndLu(user, fiche, false);
            unread.forEach(n -> n.setLu(true));
            notificationRepository.saveAll(unread);
        }
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