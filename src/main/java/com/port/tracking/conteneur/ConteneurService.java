package com.port.tracking.conteneur;

import com.port.tracking.conteneur.dto.*;
import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.fiche.FicheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConteneurService {

    private final ConteneurRepository conteneurRepository;
    private final FicheRepository ficheRepository;

    public ConteneurDTO createConteneur(Long ficheId) {
        FicheSuiveuse fiche = ficheRepository.findById(ficheId)
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        Conteneur conteneur = Conteneur.builder()
                .fiche(fiche)
                .statut(ConteneurStatut.ARRIVE)
                .build();

        return toDTO(conteneurRepository.save(conteneur));
    }

    public List<ConteneurDTO> getAllConteneurs() {
        return conteneurRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ConteneurDTO getConteneurById(Long id) {
        return toDTO(conteneurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conteneur not found")));
    }

    public List<ConteneurDTO> getConteneursByFiche(Long ficheId) {
        return conteneurRepository.findByFicheId(ficheId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ConteneurDTO assignEmplacement(Long id, AssignEmplacementRequest request) {
        Conteneur conteneur = conteneurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conteneur not found"));

        conteneur.setZone(request.getZone());
        conteneur.setRangee(request.getRangee());
        conteneur.setPosition(request.getPosition());
        conteneur.setQuai(request.getQuai());
        conteneur.setStatut(ConteneurStatut.STOCKE);

        return toDTO(conteneurRepository.save(conteneur));
    }

    public Long getDwellTime(Long id) {
        Conteneur conteneur = conteneurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conteneur not found"));

        if (conteneur.getArrivedAt() == null) return 0L;
        return Duration.between(conteneur.getArrivedAt(), LocalDateTime.now()).toHours();
    }

    private String getPriority(Conteneur conteneur) {
        List<String> classifications = getClassifications(conteneur);
        if (classifications.contains("DANGEREUSE")) return "CRITIQUE";
        if (classifications.contains("PERISSABLE"))  return "HAUTE";
        if (classifications.contains("FRAGILE"))     return "MOYENNE";
        return "NORMALE";
    }

    private Long getWarningThreshold(Conteneur conteneur) {
        List<String> classifications = getClassifications(conteneur);
        if (classifications.contains("DANGEREUSE")) return 24L;
        if (classifications.contains("PERISSABLE"))  return 24L;
        return 72L;
    }

    private Long getCritiqueThreshold(Conteneur conteneur) {
        List<String> classifications = getClassifications(conteneur);
        if (classifications.contains("DANGEREUSE")) return 48L;
        if (classifications.contains("PERISSABLE"))  return 48L;
        return 120L;
    }

    private List<String> getClassifications(Conteneur conteneur) {
        if (conteneur.getFiche() == null ||
                conteneur.getFiche().getMarchandises() == null) {
            return List.of();
        }
        return conteneur.getFiche().getMarchandises()
                .stream()
                .map(m -> m.getClassification() != null
                        ? m.getClassification().toString()
                        : "STANDARD")
                .distinct()
                .collect(Collectors.toList());
    }

    private ConteneurDTO toDTO(Conteneur conteneur) {
        Long dwellTime = conteneur.getArrivedAt() != null
                ? Duration.between(conteneur.getArrivedAt(), LocalDateTime.now()).toHours()
                : 0L;

        return ConteneurDTO.builder()
                .id(conteneur.getId())
                .ficheId(conteneur.getFiche().getId())
                .statut(conteneur.getStatut())
                .zone(conteneur.getZone())
                .rangee(conteneur.getRangee())
                .position(conteneur.getPosition())
                .quai(conteneur.getQuai())
                .arrivedAt(conteneur.getArrivedAt())
                .dwellTimeHours(dwellTime)
                .priority(getPriority(conteneur))
                .classifications(getClassifications(conteneur))
                .warningThreshold(getWarningThreshold(conteneur))
                .critiqueThreshold(getCritiqueThreshold(conteneur))
                .build();
    }
}