package com.port.tracking.document;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.fiche.FicheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FicheRepository ficheRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Document uploadDocument(Long ficheId, String type, MultipartFile file)
            throws IOException {

        FicheSuiveuse fiche = ficheRepository.findById(ficheId)
                .orElseThrow(() -> new RuntimeException("Fiche not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
                .fiche(fiche)
                .type(type)
                .filePath(filePath.toString())
                .build();

        return documentRepository.save(document);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public List<Document> getDocumentsByFiche(Long ficheId) {
        return documentRepository.findByFicheId(ficheId);
    }
}