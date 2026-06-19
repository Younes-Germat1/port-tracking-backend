package com.port.tracking.fiche;

import com.port.tracking.fiche.dto.*;
import com.port.tracking.historique.HistoriqueDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fiches")
@RequiredArgsConstructor
public class FicheController {

    private final FicheService ficheService;

    @PostMapping
    @PreAuthorize("hasRole('IMPORTATEUR') or hasRole('ADMIN')")
    public ResponseEntity<FicheDTO> createFiche(@Valid @RequestBody CreateFicheRequest request) {
        return ResponseEntity.ok(ficheService.createFiche(request));
    }

    @GetMapping
    public ResponseEntity<List<FicheDTO>> getAllFiches() {
        return ResponseEntity.ok(ficheService.getAllFiches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FicheDTO> getFicheById(@PathVariable Long id) {
        return ResponseEntity.ok(ficheService.getFicheById(id));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADII') or hasRole('ADMIN') or hasRole('OPERATEUR') or hasRole('INSPECTEUR')")
    public ResponseEntity<FicheDTO> updateStatut(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFicheStatutRequest request,
            @RequestParam Long acteurId) {
        return ResponseEntity.ok(ficheService.updateStatut(id, request, acteurId));
    }

    @PutMapping("/{id}/resoumission")
    @PreAuthorize("hasRole('IMPORTATEUR') or hasRole('ADMIN')")
    public ResponseEntity<FicheDTO> resoumission(
            @PathVariable Long id,
            @Valid @RequestBody ResoumissionRequest request) {
        return ResponseEntity.ok(ficheService.resoumission(id, request));
    }

    @GetMapping("/{id}/historique")
    public ResponseEntity<List<HistoriqueDTO>> getHistorique(@PathVariable Long id) {
        return ResponseEntity.ok(ficheService.getHistorique(id));
    }
}