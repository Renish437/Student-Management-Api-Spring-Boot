package com.renish.backend.seeder;

import com.renish.backend.entity.Student;
import com.renish.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the administrator account used to reach the /api/v1/admin/** endpoints.
 * Runs once on application startup and is idempotent: an existing account is only
 * touched when its role or password no longer match the values below.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder implements ApplicationRunner {

    @Value("${app.admin.email}")
    private String ADMIN_EMAIL;

    @Value("${app.admin.password}")
    private String ADMIN_PASSWORD;

    private static final String ADMIN_ROLE = "ADMIN";

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!studentRepository.existsByEmail(ADMIN_EMAIL)) {
            Student admin = Student.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .role(ADMIN_ROLE)
                    .build();

            studentRepository.save(admin);
            log.info("UserSeeder: created admin user {}", ADMIN_EMAIL);
            return;
        }

        Student existing = studentRepository.findByEmail(ADMIN_EMAIL).orElseThrow();
        boolean changed = false;

        if (!ADMIN_ROLE.equals(existing.getRole())) {
            existing.setRole(ADMIN_ROLE);
            changed = true;
        }

        if (existing.getPassword() == null
                || !passwordEncoder.matches(ADMIN_PASSWORD, existing.getPassword())) {
            existing.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            changed = true;
        }

        if (changed) {
            studentRepository.save(existing);
            log.info("UserSeeder: updated admin user {}", ADMIN_EMAIL);
        } else {
            log.debug("UserSeeder: admin user {} is already up to date", ADMIN_EMAIL);
        }
    }
}
