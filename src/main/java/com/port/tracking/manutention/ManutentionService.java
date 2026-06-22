package com.port.tracking.manutention;

import com.port.tracking.conteneur.Conteneur;
import com.port.tracking.conteneur.ConteneurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManutentionService {

    private final ManutentionRepository manutentionRepository;
    private final ConteneurRepository conteneurRepository;

    public ManutentionDTO createManutention(Long conteneurId, ManutentionRequest request) {
        Conteneur conteneur = conteneurRepository.findById(conteneurId)
                .orElseThrow(() -> new RuntimeException("Conteneur not found"));

        Manutention manutention = Manutention.builder()
                .conteneur(conteneur)
                .type(request.getType())
                .datePrevue(request.getDatePrevue())
                .heurePrevue(request.getHeurePrevue())
                .build();

        return toDTO(manutentionRepository.save(manutention));
    }

    public List<ManutentionDTO> getManutentionsByConteneur(Long conteneurId) {
        return manutentionRepository
                .findByConteneurIdOrderByDatePrevueAscHeurePrevueAsc(conteneurId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteManutention(Long id) {
        manutentionRepository.deleteById(id);
    }

    private ManutentionDTO toDTO(Manutention m) {
        return ManutentionDTO.builder()
                .id(m.getId())
                .conteneurId(m.getConteneur().getId())
                .type(m.getType())
                .datePrevue(m.getDatePrevue())
                .heurePrevue(m.getHeurePrevue())
                .createdAt(m.getCreatedAt())
                .build();
    }
}