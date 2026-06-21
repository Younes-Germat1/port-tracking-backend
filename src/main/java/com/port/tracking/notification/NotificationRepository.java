package com.port.tracking.notification;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndLu(User user, Boolean lu);
    List<Notification> findByUserAndFicheAndLu(User user, FicheSuiveuse fiche, Boolean lu);
    List<Notification> findByFicheAndLu(FicheSuiveuse fiche, Boolean lu);
}