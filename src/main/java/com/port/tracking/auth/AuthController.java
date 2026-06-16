package com.port.tracking.auth;

import com.port.tracking.auth.dto.AuthResponse;
import com.port.tracking.auth.dto.LoginRequest;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user,
                                                 @RequestParam UserRole role) {
        return ResponseEntity.ok(authService.register(user, role));
    }

    // ⚠️ TEMPORAIRE — à supprimer après test
    @GetMapping("/test-hash")
    public ResponseEntity<String> testHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        return ResponseEntity.ok(hash);
    }
}