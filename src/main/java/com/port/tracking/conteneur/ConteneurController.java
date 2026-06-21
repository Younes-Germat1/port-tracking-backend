package com.port.tracking.conteneur;

import com.port.tracking.conteneur.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> assignEmplacement(
            @PathVariable Long id,
            @RequestBody AssignEmplacementRequest request) {
        try {
            return ResponseEntity.ok(conteneurService.assignEmplacement(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/dwell-time")
    public ResponseEntity<Long> getDwellTime(@PathVariable Long id) {
        return ResponseEntity.ok(conteneurService.getDwellTime(id));
    }
}