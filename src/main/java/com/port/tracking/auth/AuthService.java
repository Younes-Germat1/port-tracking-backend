package com.port.tracking.auth;

import com.port.tracking.auth.dto.AuthResponse;
import com.port.tracking.auth.dto.LoginRequest;
import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .nom(user.getNom())
                .build();
    }

    public AuthResponse register(User user, UserRole role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .nom(user.getNom())
                .build();
    }
}