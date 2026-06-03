package com.port.tracking.fiche;

import com.port.tracking.fiche.dto.*;
import com.port.tracking.historique.Historique;
import com.port.tracking.historique.HistoriqueRepository;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FicheService {

    private final FicheRepository ficheRepository;
    private final UserRepository userRepository;
    private final HistoriqueRepository historiqueRepository;

    public FicheDTO createFiche(CreateFicheRequest request) {
        User importateur = userRepository.findById(request.getImportateurId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        FicheSuiveuse fiche = FicheSuiveuse.builder()
                .importateur(importateur)
                .statut(FicheStatut.EN_ATTENTE)
                .build();

        FicheSuiveuse saved = ficheRepository.save(fiche);

        historiqueRepository.save(Historique.builder()
                .fiche(saved)
                .acteur(importateur)
                .action("CREATION")
                .details("Fiche créée par " + importateur.getNom())
                .build());

        return toDTO(saved);
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

    public FicheDTO updateStatut(Long id, UpdateFicheStatutRequest request, Long acteurId) {
        FicheSuiveuse fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        User acteur = userRepository.findById(acteurId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        fiche.setStatut(request.getStatut());
        FicheSuiveuse saved = ficheRepository.save(fiche);

        historiqueRepository.save(Historique.builder()
                .fiche(saved)
                .acteur(acteur)
                .action("CHANGEMENT_STATUT")
                .details("Statut changé à " + request.getStatut())
                .build());

        return toDTO(saved);
    }

    public List<Historique> getHistorique(Long ficheId) {
        return historiqueRepository.findByFicheIdOrderByTimestampDesc(ficheId);
    }

    private FicheDTO toDTO(FicheSuiveuse fiche) {
        return FicheDTO.builder()
                .id(fiche.getId())
                .importateurId(fiche.getImportateur().getId())
                .importateurNom(fiche.getImportateur().getNom())
                .statut(fiche.getStatut())
                .createdAt(fiche.getCreatedAt())
                .updatedAt(fiche.getUpdatedAt())
                .build();
    }
}