package com.port.tracking.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.port.tracking.conteneur.Conteneur;
import com.port.tracking.conteneur.ConteneurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final ConteneurRepository conteneurRepository;

    public byte[] generateQrCode(Long conteneurId) throws Exception {
        Conteneur conteneur = conteneurRepository.findById(conteneurId)
                .orElseThrow(() -> new RuntimeException("Conteneur not found"));

        String content = String.format(
                "CONTENEUR_ID:%d | FICHE_ID:%d | STATUT:%s | ZONE:%s | POSITION:%s",
                conteneur.getId(),
                conteneur.getFiche().getId(),
                conteneur.getStatut(),
                conteneur.getZone() != null ? conteneur.getZone() : "N/A",
                conteneur.getPosition() != null ? conteneur.getPosition() : "N/A"
        );

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrCodeWriter.encode(
                content, BarcodeFormat.QR_CODE, 300, 300, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}