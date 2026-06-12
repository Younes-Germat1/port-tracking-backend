package com.port.tracking.inspection;

import com.port.tracking.inspection.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping
    @PreAuthorize("hasRole('ADII') or hasRole('ADMIN')")
    public ResponseEntity<InspectionDTO> createInspection(
            @RequestParam Long conteneurId,
            @RequestParam Long inspecteurId,
            @RequestParam String organisme) {
        return ResponseEntity.ok(
                inspectionService.createInspection(conteneurId, inspecteurId, organisme));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADII') or hasRole('INSPECTEUR')")
    public ResponseEntity<List<InspectionDTO>> getAllInspections() {
        return ResponseEntity.ok(inspectionService.getAllInspections());
    }

    @GetMapping("/mes-taches")
    @PreAuthorize("hasRole('INSPECTEUR') or hasRole('ADMIN') or hasRole('ADII')")
    public ResponseEntity<List<InspectionDTO>> getMesTaches(@RequestParam Long inspecteurId) {
        return ResponseEntity.ok(inspectionService.getMesTaches(inspecteurId));
    }

    @PutMapping("/{id}/resultat")
    @PreAuthorize("hasRole('INSPECTEUR') or hasRole('ADMIN') or hasRole('ADII')")
    public ResponseEntity<InspectionDTO> enregistrerResultat(
            @PathVariable Long id,
            @Valid @RequestBody EnregistrerResultatRequest request) {
        return ResponseEntity.ok(inspectionService.enregistrerResultat(id, request));
    }
}