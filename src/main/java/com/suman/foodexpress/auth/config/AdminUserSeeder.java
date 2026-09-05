package com.suman.foodexpress.auth.config;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.entity.UserRole;
import com.suman.foodexpress.auth.entity.UserStatus;
import com.suman.foodexpress.auth.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AdminUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String email;
    private final String configuredPassword;

    public AdminUserSeeder(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.seed.admin.enabled}") boolean enabled,
                           @Value("${app.seed.admin.email}") String email,
                           @Value("${app.seed.admin.password:}") String configuredPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.email = email;
        this.configuredPassword = configuredPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            return;
        }
        boolean generatedPassword = configuredPassword == null || configuredPassword.isBlank();
        String rawPassword = generatedPassword ? generatePassword(16) : configuredPassword;
        Instant now = Instant.now();
        User admin = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .name("Administrator")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .verifiedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        try {
            userRepository.save(admin);
        } catch (DataIntegrityViolationException ex) {
            log.warn("[SEED ADMIN] admin user {} was created concurrently; skipping", email);
            return;
        }
        if (generatedPassword) {
            log.info("[SEED ADMIN] created admin user {} with generated password: {}", email, rawPassword);
        } else {
            log.info("[SEED ADMIN] created admin user {}", email);
        }
    }

    private static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}