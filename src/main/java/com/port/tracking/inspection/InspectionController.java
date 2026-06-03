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

    @GetMapping("/mes-taches")
    @PreAuthorize("hasRole('INSPECTEUR') or hasRole('ADMIN')")
    public ResponseEntity<List<InspectionDTO>> getMesTaches(@RequestParam Long inspecteurId) {
        return ResponseEntity.ok(inspectionService.getMesTaches(inspecteurId));
    }

    @PutMapping("/{id}/resultat")
    @PreAuthorize("hasRole('INSPECTEUR') or hasRole('ADMIN')")
    public ResponseEntity<InspectionDTO> enregistrerResultat(
            @PathVariable Long id,
            @Valid @RequestBody EnregistrerResultatRequest request) {
        return ResponseEntity.ok(inspectionService.enregistrerResultat(id, request));
    }
}