package com.port.tracking.qrcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @GetMapping(value = "/{conteneurId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long conteneurId) throws Exception {
        byte[] qrCode = qrCodeService.generateQrCode(conteneurId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}