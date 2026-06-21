package com.port.tracking.fiche;

import com.port.tracking.fiche.dto.*;
import com.port.tracking.historique.Historique;
import com.port.tracking.historique.HistoriqueDTO;
import com.port.tracking.historique.HistoriqueRepository;
import com.port.tracking.marchandise.Marchandise;
import com.port.tracking.marchandise.MarchandiseRepository;
import com.port.tracking.notification.NotificationService;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FicheService {

    private final FicheRepository ficheRepository;
    private final MarchandiseRepository marchandiseRepository;
    private final UserRepository userRepository;
    private final HistoriqueRepository historiqueRepository;
    private final NotificationService notificationService;

    @Transactional
    public FicheDTO createFiche(CreateFicheRequest request) {
        User importateur = userRepository.findById(request.getImportateurId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        FicheSuiveuse fiche = FicheSuiveuse.builder()
                .importateur(importateur)
                .importateurNom(request.getImportateurNom())
                .importateurAdresse(request.getImportateurAdresse())
                .importateurContact(request.getImportateurContact())
                .statut(FicheStatut.EN_ATTENTE)
                .organismes(request.getOrganismes() != null ? request.getOrganismes() : List.of())
                .build();

        FicheSuiveuse saved = ficheRepository.save(fiche);

        if (request.getMarchandises() != null) {
            request.getMarchandises().forEach(m -> {
                marchandiseRepository.save(Marchandise.builder()
                        .fiche(saved)
                        .description(m.getDescription())
                        .classification(m.getClassification())
                        .codeSH(m.getCodeSH())
                        .poids(m.getPoids())
                        .volume(m.getVolume())
                        .build());
            });
        }

        historiqueRepository.save(Historique.builder()
                .fiche(saved)
                .acteur(importateur)
                .action("CREATION")
                .details("Fiche créée par " + importateur.getNom())
                .build());

        List<User> adiiAgents = userRepository.findByRole(UserRole.ADII);
        for (User adii : adiiAgents) {
            notificationService.createNotification(
                    adii,
                    saved,
                    "📋 Nouvelle fiche #" + saved.getId() + " soumise par " + importateur.getNom() + " — en attente de validation."
            );
        }

        return toDTO(ficheRepository.findById(saved.getId()).orElse(saved));
    }

    public List<FicheDTO> getAllFiches() {
        return ficheRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FicheDTO getFicheById(Long id) {
        return toDTO(ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche not found")));
    }

    @Transactional
    public FicheDTO updateStatut(Long id, UpdateFicheStatutRequest request, Long acteurId) {
        FicheSuiveuse fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        User acteur = userRepository.findById(acteurId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        fiche.setStatut(request.getStatut());
        fiche.setDelayAlertSent(false);
        FicheSuiveuse saved = ficheRepository.save(fiche);

        historiqueRepository.save(Historique.builder()
                .fiche(saved)
                .acteur(acteur)
                .action("CHANGEMENT_STATUT")
                .details("Statut changé à " + request.getStatut()
                        + (request.getMotif() != null ? " — " + request.getMotif() : ""))
                .build());

        // ✅ Clear ADII's "new fiche" notification — they just acted on it
        if (request.getStatut() == FicheStatut.APPROUVEE || request.getStatut() == FicheStatut.REJETEE) {
            List<User> adiiAgents = userRepository.findByRole(UserRole.ADII);
            notificationService.clearNotificationsForFicheAndRole(saved, adiiAgents);
        }

        String message = buildNotificationMessage(request.getStatut());
        notificationService.createNotification(fiche.getImportateur(), saved, message);

        if (request.getStatut() == FicheStatut.APPROUVEE) {
            List<User> operateurs = userRepository.findByRole(UserRole.OPERATEUR);
            for (User operateur : operateurs) {
                notificationService.createNotification(
                        operateur,
                        saved,
                        "📦 Nouvelle fiche #" + saved.getId() + " approuvée — en attente de placement."
                );
            }
        }

        // ✅ Clear operator's "to place" notification when fiche becomes PLACEE
        if (request.getStatut() == FicheStatut.PLACEE) {
            List<User> operateurs = userRepository.findByRole(UserRole.OPERATEUR);
            notificationService.clearNotificationsForFicheAndRole(saved, operateurs);
        }

        if (request.getStatut() == FicheStatut.DEDOUANEE) {
            List<User> operateurs = userRepository.findByRole(UserRole.OPERATEUR);
            for (User operateur : operateurs) {
                notificationService.createNotification(
                        operateur,
                        saved,
                        "✅ Fiche #" + saved.getId() + " dédouanée — prête pour libération."
                );
            }
        }

        // ✅ Clear operator's "dedouanee, ready to liberate" notification when LIBEREE
        if (request.getStatut() == FicheStatut.LIBEREE) {
            List<User> operateurs = userRepository.findByRole(UserRole.OPERATEUR);
            notificationService.clearNotificationsForFicheAndRole(saved, operateurs);
        }

        return toDTO(saved);
    }

    @Transactional
    public FicheDTO resoumission(Long id, ResoumissionRequest request) {
        FicheSuiveuse fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        fiche.setImportateurNom(request.getImportateurNom());
        fiche.setImportateurAdresse(request.getImportateurAdresse());
        fiche.setImportateurContact(request.getImportateurContact());
        fiche.setStatut(FicheStatut.EN_ATTENTE);
        fiche.setDelayAlertSent(false);

        FicheSuiveuse saved = ficheRepository.save(fiche);

        historiqueRepository.save(Historique.builder()
                .fiche(saved)
                .acteur(fiche.getImportateur())
                .action("RESOUMISSION")
                .details("Fiche re-soumise par " + fiche.getImportateur().getNom())
                .build());

        // ✅ New fresh "to-do" notification for ADII on resubmission
        List<User> adiiAgents = userRepository.findByRole(UserRole.ADII);
        for (User adii : adiiAgents) {
            notificationService.createNotification(
                    adii,
                    saved,
                    "🔄 Fiche #" + saved.getId() + " re-soumise par " + fiche.getImportateur().getNom() + " après rejet — à re-valider."
            );
        }

        return toDTO(saved);
    }

    public List<HistoriqueDTO> getHistorique(Long ficheId) {
        return historiqueRepository.findByFicheIdOrderByTimestampDesc(ficheId)
                .stream()
                .map(h -> HistoriqueDTO.builder()
                        .id(h.getId())
                        .action(h.getAction())
                        .details(h.getDetails())
                        .timestamp(h.getTimestamp())
                        .acteurNom(h.getActeur() != null ? h.getActeur().getNom() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private String buildNotificationMessage(FicheStatut statut) {
        return switch (statut) {
            case APPROUVEE -> "✅ Votre fiche a été approuvée par l'ADII.";
            case REJETEE   -> "❌ Votre fiche a été rejetée par l'ADII.";
            case PLACEE    -> "📦 Un emplacement a été assigné à votre conteneur.";
            case DEDOUANEE -> "🔍 Votre marchandise a été dédouanée.";
            case LIBEREE   -> "🎉 Votre marchandise est libérée et prête pour enlèvement !";
            default        -> "🔔 Le statut de votre fiche a changé.";
        };
    }

    private FicheDTO toDTO(FicheSuiveuse fiche) {
        List<FicheDTO.MarchandiseDTO> marchandiseDTOs = fiche.getMarchandises() == null ? List.of() :
                fiche.getMarchandises().stream()
                        .map(m -> FicheDTO.MarchandiseDTO.builder()
                                .id(m.getId())
                                .description(m.getDescription())
                                .classification(m.getClassification())
                                .codeSH(m.getCodeSH())
                                .poids(m.getPoids())
                                .volume(m.getVolume())
                                .build())
                        .collect(Collectors.toList());

        return FicheDTO.builder()
                .id(fiche.getId())
                .importateurId(fiche.getImportateur().getId())
                .importateurNom(fiche.getImportateurNom())
                .importateurAdresse(fiche.getImportateurAdresse())
                .importateurContact(fiche.getImportateurContact())
                .statut(fiche.getStatut())
                .marchandises(marchandiseDTOs)
                .organismes(fiche.getOrganismes())
                .createdAt(fiche.getCreatedAt())
                .updatedAt(fiche.getUpdatedAt())
                .build();
    }
}