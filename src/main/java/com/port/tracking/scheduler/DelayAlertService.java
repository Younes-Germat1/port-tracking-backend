package com.port.tracking.scheduler;

import com.port.tracking.fiche.FicheRepository;
import com.port.tracking.fiche.FicheStatut;
import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.inspection.Inspection;
import com.port.tracking.inspection.InspectionRepository;
import com.port.tracking.notification.NotificationService;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DelayAlertService {

    private static final long DELAY_THRESHOLD_HOURS = 48;

    private final FicheRepository ficheRepository;
    private final InspectionRepository inspectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 3600000)
    public void checkDelays() {
        log.info("Running delay alert check...");
        checkAdiiDelays();
        checkOperateurDelays();
        checkInspecteurDelays();
    }

    private void checkAdiiDelays() {
        List<FicheSuiveuse> fiches = ficheRepository.findByStatut(FicheStatut.EN_ATTENTE);
        List<User> adiiAgents = userRepository.findByRole(UserRole.ADII);

        for (FicheSuiveuse fiche : fiches) {
            if (Boolean.TRUE.equals(fiche.getDelayAlertSent())) continue;

            long hoursWaiting = Duration.between(fiche.getUpdatedAt(), LocalDateTime.now()).toHours();
            if (hoursWaiting >= DELAY_THRESHOLD_HOURS) {
                for (User adii : adiiAgents) {
                    notificationService.createNotification(
                            adii,
                            fiche,
                            "⏰ RETARD — Fiche #" + fiche.getId() + " en attente depuis " + hoursWaiting + "h. Action requise."
                    );
                }
                fiche.setDelayAlertSent(true);
                ficheRepository.save(fiche);
            }
        }
    }

    private void checkOperateurDelays() {
        List<FicheSuiveuse> fiches = ficheRepository.findByStatut(FicheStatut.APPROUVEE);
        List<User> operateurs = userRepository.findByRole(UserRole.OPERATEUR);

        for (FicheSuiveuse fiche : fiches) {
            if (Boolean.TRUE.equals(fiche.getDelayAlertSent())) continue;

            long hoursWaiting = Duration.between(fiche.getUpdatedAt(), LocalDateTime.now()).toHours();
            if (hoursWaiting >= DELAY_THRESHOLD_HOURS) {
                for (User operateur : operateurs) {
                    notificationService.createNotification(
                            operateur,
                            fiche,
                            "⏰ RETARD — Fiche #" + fiche.getId() + " approuvée depuis " + hoursWaiting + "h sans emplacement assigné."
                    );
                }
                fiche.setDelayAlertSent(true);
                ficheRepository.save(fiche);
            }
        }
    }

    private void checkInspecteurDelays() {
        List<Inspection> pendingInspections = inspectionRepository.findByResultatIsNull();

        for (Inspection inspection : pendingInspections) {
            if (Boolean.TRUE.equals(inspection.getDelayAlertSent())) continue;
            if (inspection.getInspecteur() == null) continue;

            long hoursWaiting = Duration.between(inspection.getDate(), LocalDateTime.now()).toHours();
            if (hoursWaiting >= DELAY_THRESHOLD_HOURS) {
                notificationService.createNotification(
                        inspection.getInspecteur(),
                        inspection.getConteneur().getFiche(),
                        "⏰ RETARD — Inspection #" + inspection.getId() + " en attente depuis " + hoursWaiting + "h. Veuillez la traiter."
                );
                inspection.setDelayAlertSent(true);
                inspectionRepository.save(inspection);
            }
        }
    }
}