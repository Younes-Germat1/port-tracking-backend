package com.port.tracking.conteneur;

import com.port.tracking.conteneur.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/conteneurs")
@RequiredArgsConstructor
public class ConteneurController {

    private final ConteneurService conteneurService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<ConteneurDTO> createConteneur(@RequestParam Long ficheId) {
        return ResponseEntity.ok(conteneurService.createConteneur(ficheId));
    }

    @GetMapping
    public ResponseEntity<List<ConteneurDTO>> getAllConteneurs() {
        return ResponseEntity.ok(conteneurService.getAllConteneurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteneurDTO> getConteneurById(@PathVariable Long id) {
        return ResponseEntity.ok(conteneurService.getConteneurById(id));
    }

    @GetMapping("/fiche/{ficheId}")
    public ResponseEntity<List<ConteneurDTO>> getConteneursByFiche(@PathVariable Long ficheId) {
        return ResponseEntity.ok(conteneurService.getConteneursByFiche(ficheId));
    }

    @PutMapping("/{id}/emplacement")
    @PreAuthorize("hasRole('OPERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<ConteneurDTO> assignEmplacement(
            @PathVariable Long id,
            @RequestBody AssignEmplacementRequest request) {
        return ResponseEntity.ok(conteneurService.assignEmplacement(id, request));
    }

    @GetMapping("/{id}/dwell-time")
    public ResponseEntity<Long> getDwellTime(@PathVariable Long id) {
        return ResponseEntity.ok(conteneurService.getDwellTime(id));
    }
}