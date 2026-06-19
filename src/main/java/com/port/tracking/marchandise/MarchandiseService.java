package com.port.tracking.marchandise;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.fiche.FicheRepository;
import com.port.tracking.marchandise.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarchandiseService {

    private final MarchandiseRepository marchandiseRepository;
    private final FicheRepository ficheRepository;

    public MarchandiseDTO createMarchandise(CreateMarchandiseRequest request) {
        FicheSuiveuse fiche = ficheRepository.findById(request.getFicheId())
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        Marchandise marchandise = Marchandise.builder()
                .fiche(fiche)
                .description(request.getDescription())
                .classification(request.getClassification() != null
                        ? request.getClassification().toString()
                        : null)
                .poids(request.getPoids())
                .volume(request.getVolume())
                .codeSH(request.getCodeSh())
                .build();

        return toDTO(marchandiseRepository.save(marchandise));
    }

    public MarchandiseDTO getMarchandiseById(Long id) {
        return toDTO(marchandiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marchandise not found")));
    }

    public List<MarchandiseDTO> getMarchandisesByFiche(Long ficheId) {
        return marchandiseRepository.findByFicheId(ficheId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private MarchandiseDTO toDTO(Marchandise m) {
        return MarchandiseDTO.builder()
                .id(m.getId())
                .ficheId(m.getFiche().getId())
                .description(m.getDescription())
                .classification(m.getClassification())
                .poids(m.getPoids())
                .volume(m.getVolume())
                .codeSh(m.getCodeSH())
                .build();
    }
}