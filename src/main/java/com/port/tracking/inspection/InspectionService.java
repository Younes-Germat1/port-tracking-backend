package com.port.tracking.inspection;

import com.port.tracking.conteneur.Conteneur;
import com.port.tracking.conteneur.ConteneurRepository;
import com.port.tracking.conteneur.ConteneurStatut;
import com.port.tracking.inspection.dto.*;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final ConteneurRepository conteneurRepository;
    private final UserRepository userRepository;

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

        return toDTO(inspectionRepository.save(inspection));
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
        return inspectionRepository.findByInspecteur(inspecteur)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
        inspection.setOrganisme(request.getOrganisme());

        Conteneur conteneur = inspection.getConteneur();
        conteneur.setStatut(ConteneurStatut.STOCKE);
        conteneurRepository.save(conteneur);

        return toDTO(inspectionRepository.save(inspection));
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
                .build();
    }
}