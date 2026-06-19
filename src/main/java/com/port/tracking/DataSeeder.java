package com.port.tracking;

import com.port.tracking.user.User;
import com.port.tracking.user.UserRepository;
import com.port.tracking.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createIfNotExists("Importateur Test", "importateur@port.ma", "123456", UserRole.IMPORTATEUR);
        createIfNotExists("Agent ADII",       "adii@port.ma",        "123456", UserRole.ADII);
        createIfNotExists("Operateur Port",   "operateur@port.ma",   "123456", UserRole.OPERATEUR);
        createIfNotExists("Inspecteur Test",  "inspecteur@port.ma",  "123456", UserRole.INSPECTEUR);
        createIfNotExists("Admin",            "admin@port.ma",       "123456", UserRole.ADMIN);
    }

    private void createIfNotExists(String nom, String email, String password, UserRole role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            userRepository.save(User.builder()
                    .nom(nom)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build());
        }
    }
}