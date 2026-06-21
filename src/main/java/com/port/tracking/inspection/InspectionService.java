package com.port.tracking.inspection;

import com.port.tracking.conteneur.Conteneur;
import com.port.tracking.conteneur.ConteneurRepository;
import com.port.tracking.conteneur.ConteneurStatut;
import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.inspection.dto.*;
import com.port.tracking.notification.NotificationService;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final ConteneurRepository conteneurRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final String UPLOAD_DIR = "uploads/inspections/";

    public InspectionDTO createInspection(Long conteneurId, Long inspecteurId, String organisme) {
        Conteneur conteneur = conteneurRepository.findById(conteneurId)
                .orElseThrow(() -> new RuntimeException("Conteneur not found"));

        User inspecteur = userRepository.findById(inspecteurId)
                .orElseThrow(() -> new RuntimeException("Inspecteur not found"));

        conteneur.setStatut(ConteneurStatut.EN_INSPECTION);
        conteneurRepository.save(conteneur);

        Inspection inspection = Inspection.builder()
                .conteneur(conteneur)
                .inspecteur(inspecteur)
                .organisme(organisme)
                .date(LocalDateTime.now())
                .build();

        Inspection saved = inspectionRepository.save(inspection);

        notificationService.createNotification(
                inspecteur,
                conteneur.getFiche(),
                "🔍 Nouvelle inspection assignée — Conteneur #" + conteneur.getId()
                        + " (" + (organisme != null ? organisme : "organisme non précisé") + ")"
        );

        return toDTO(saved);
    }

    /**
     * ✅ Auto-assignment: called when a conteneur is placed (PLACEE).
     * For each organisme required by the fiche (excluding ADII, which is a customs
     * validation step and not a field inspection), automatically pick the least-busy
     * inspector registered for that organisme and create an Inspection for them.
     * If no inspector is registered for a given organisme yet, that organisme is
     * skipped (logged via no-op) rather than failing the whole placement.
     */
    public void autoCreateInspectionsForConteneur(Conteneur conteneur) {
        FicheSuiveuse fiche = conteneur.getFiche();
        if (fiche == null || fiche.getOrganismes() == null) return;

        List<String> organismesAInspecter = fiche.getOrganismes().stream()
                .filter(o -> !"ADII".equals(o))
                .distinct()
                .collect(Collectors.toList());

        List<User> tousInspecteurs = userRepository.findByRole(UserRole.INSPECTEUR);

        for (String organisme : organismesAInspecter) {
            List<User> candidats = tousInspecteurs.stream()
                    .filter(u -> organisme.equals(u.getOrganisme()))
                    .collect(Collectors.toList());

            if (candidats.isEmpty()) {
                // No inspector registered for this organisme yet — admin needs to create one.
                continue;
            }

            // Pick the inspector with the fewest currently-pending inspections (load balancing)
            User chosen = candidats.stream()
                    .min(Comparator.comparingLong(u ->
                            inspectionRepository.findByInspecteur(u).stream()
                                    .filter(i -> i.getResultat() == null)
                                    .count()))
                    .orElse(candidats.get(0));

            createInspection(conteneur.getId(), chosen.getId(), organisme);
        }
    }

    public List<InspectionDTO> getAllInspections() {
        return inspectionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<InspectionDTO> getMesTaches(Long inspecteurId) {
        User inspecteur = userRepository.findById(inspecteurId)
                .orElseThrow(() -> new RuntimeException("Inspecteur not found"));

        List<InspectionDTO> all = inspectionRepository.findByInspecteur(inspecteur)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        List<InspectionDTO> pending = all.stream()
                .filter(i -> i.getResultat() == null)
                .sorted(Comparator.comparingLong(
                        (InspectionDTO i) -> i.getDwellTimeHours() != null ? i.getDwellTimeHours() : 0L
                ).reversed())
                .collect(Collectors.toList());

        List<InspectionDTO> done = all.stream()
                .filter(i -> i.getResultat() != null)
                .collect(Collectors.toList());

        pending.addAll(done);
        return pending;
    }

    public InspectionDTO getInspectionById(Long id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        return toDTO(inspection);
    }

    public InspectionDTO enregistrerResultat(Long id, EnregistrerResultatRequest request) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        inspection.setResultat(request.getResultat());
        inspection.setCommentaire(request.getCommentaire());

        if (request.getOrganisme() != null && !request.getOrganisme().isBlank()) {
            inspection.setOrganisme(request.getOrganisme());
        }

        Conteneur conteneur = inspection.getConteneur();
        conteneur.setStatut(ConteneurStatut.STOCKE);
        conteneurRepository.save(conteneur);

        return toDTO(inspectionRepository.save(inspection));
    }

    public InspectionDTO uploadPhoto(Long id, MultipartFile file) throws IOException {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        inspection.setPhotoPath(UPLOAD_DIR + fileName);
        return toDTO(inspectionRepository.save(inspection));
    }

    private Long calculateDwellTime(Conteneur conteneur) {
        if (conteneur.getArrivedAt() == null) return 0L;
        return Duration.between(conteneur.getArrivedAt(), LocalDateTime.now()).toHours();
    }

    private List<InspectionDTO.MarchandiseInfo> getMarchandises(Conteneur conteneur) {
        if (conteneur.getFiche() == null ||
                conteneur.getFiche().getMarchandises() == null) {
            return List.of();
        }
        return conteneur.getFiche().getMarchandises().stream()
                .map(m -> InspectionDTO.MarchandiseInfo.builder()
                        .id(m.getId())
                        .description(m.getDescription())
                        .classification(m.getClassification() != null
                                ? m.getClassification().toString() : null)
                        .codeSH(m.getCodeSH())
                        .poids(m.getPoids())
                        .volume(m.getVolume())
                        .build())
                .collect(Collectors.toList());
    }

    private InspectionDTO toDTO(Inspection inspection) {
        return InspectionDTO.builder()
                .id(inspection.getId())
                .conteneurId(inspection.getConteneur().getId())
                .inspecteurId(inspection.getInspecteur() != null ?
                        inspection.getInspecteur().getId() : null)
                .inspecteurNom(inspection.getInspecteur() != null ?
                        inspection.getInspecteur().getNom() : null)
                .organisme(inspection.getOrganisme())
                .resultat(inspection.getResultat())
                .date(inspection.getDate())
                .commentaire(inspection.getCommentaire())
                .dwellTimeHours(calculateDwellTime(inspection.getConteneur()))
                .photoPath(inspection.getPhotoPath())
                .marchandises(getMarchandises(inspection.getConteneur()))
                .build();
    }
}