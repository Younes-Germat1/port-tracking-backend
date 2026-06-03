package com.port.tracking.document;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('IMPORTATEUR') or hasRole('ADMIN')")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam Long ficheId,
            @RequestParam String type,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(ficheId, type, file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id)
            throws MalformedURLException {
        Document document = documentService.getDocumentById(id);
        Resource resource = new UrlResource(
                Paths.get(document.getFilePath()).toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/fiche/{ficheId}")
    public ResponseEntity<List<Document>> getDocumentsByFiche(@PathVariable Long ficheId) {
        return ResponseEntity.ok(documentService.getDocumentsByFiche(ficheId));
    }
}