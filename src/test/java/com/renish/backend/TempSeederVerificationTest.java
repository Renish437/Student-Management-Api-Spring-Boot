package com.renish.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.renish.backend.config.AuditorAwareImpl;
import com.renish.backend.entity.Student;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.seeder.UserSeeder;

@SpringBootTest
@ActiveProfiles("test")
class TempSeederVerificationTest {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserSeeder userSeeder;
    @Autowired
    private AuditorAwareImpl auditorAware;

    @Test
    void seederCreatesAdminLoginableUser() {
        assertEquals("renishsiwakoti437@gmail.com", adminEmail, "app.admin.email is not the requested admin");
        assertEquals("Renish@123", adminPassword, "app.admin.password is not the requested admin");

        Student admin = studentRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AssertionError("admin user was not seeded"));

        assertEquals("ADMIN", admin.getRole());
        assertTrue(passwordEncoder.matches(adminPassword, admin.getPassword()),
                "seeded password does not match");

        UserDetails details = userDetailsService.loadUserByUsername(adminEmail);
        assertTrue(details.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "expected ROLE_ADMIN but was " + details.getAuthorities());

        // idempotent: a second run must not duplicate or break the row
        userSeeder.run(null);
        assertEquals(1, studentRepository.count());
    }

    @Test
    void auditorReturnsAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        adminEmail, "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        try {
            assertEquals(Optional.of(adminEmail), auditorAware.getCurrentAuditor());
        } finally {
            SecurityContextHolder.clearContext();
        }

        assertEquals(Optional.empty(), auditorAware.getCurrentAuditor());
    }
}
