package com.port.tracking.manutention;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manutentions")
@RequiredArgsConstructor
public class ManutentionController {

    private final ManutentionService manutentionService;

    @PostMapping("/conteneur/{conteneurId}")
    @PreAuthorize("hasRole('OPERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<ManutentionDTO> create(
            @PathVariable Long conteneurId,
            @RequestBody ManutentionRequest request) {
        return ResponseEntity.ok(manutentionService.createManutention(conteneurId, request));
    }

    @GetMapping("/conteneur/{conteneurId}")
    public ResponseEntity<List<ManutentionDTO>> getByConteneur(@PathVariable Long conteneurId) {
        return ResponseEntity.ok(manutentionService.getManutentionsByConteneur(conteneurId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        manutentionService.deleteManutention(id);
        return ResponseEntity.noContent().build();
    }
}