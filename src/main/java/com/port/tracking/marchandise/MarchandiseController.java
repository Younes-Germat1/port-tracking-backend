package com.port.tracking.marchandise;

import com.port.tracking.marchandise.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/marchandises")
@RequiredArgsConstructor
public class MarchandiseController {

    private final MarchandiseService marchandiseService;

    @PostMapping
    @PreAuthorize("hasRole('IMPORTATEUR') or hasRole('ADMIN')")
    public ResponseEntity<MarchandiseDTO> createMarchandise(
            @Valid @RequestBody CreateMarchandiseRequest request) {
        return ResponseEntity.ok(marchandiseService.createMarchandise(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarchandiseDTO> getMarchandiseById(@PathVariable Long id) {
        return ResponseEntity.ok(marchandiseService.getMarchandiseById(id));
    }

    @GetMapping("/fiche/{ficheId}")
    public ResponseEntity<List<MarchandiseDTO>> getMarchandisesByFiche(
            @PathVariable Long ficheId) {
        return ResponseEntity.ok(marchandiseService.getMarchandisesByFiche(ficheId));
    }
}