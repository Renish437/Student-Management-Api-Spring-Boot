package com.renish.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.renish.backend.entity.Student;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.service.security.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityResponseIntegrationTest {

    private static final String STUDENTS_URL = "/api/v1/students";
    private static final String LOGIN_URL = "/api/v1/auth/login";

    private static final String AUTH_REQUIRED =
            "Authentication is required. Please login to get a valid token.";
    private static final String INVALID_TOKEN =
            "Invalid token or expired token. Please login again.";
    private static final String EXPIRED_TOKEN =
            "Token has expired. Please login again to get a new token.";
    private static final String BAD_CREDENTIALS =
            "Invalid email or password. Please try again.";
    private static final String ACCESS_DENIED =
            "Access denied. You are not authorized to access this resource.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String secret;

    @Test
    void shouldReturnAuthenticationRequiredWhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get(STUDENTS_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(AUTH_REQUIRED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value(STUDENTS_URL))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnInvalidTokenWhenAuthorizationHeaderIsMalformed() throws Exception {
        mockMvc.perform(get(STUDENTS_URL).header("Authorization", "Token abc.def.ghi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(
                        "Invalid authorization header. Expected format: Bearer <token>"))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturnInvalidTokenWhenTokenIsMalformed() throws Exception {
        mockMvc.perform(get(STUDENTS_URL).header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(INVALID_TOKEN))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturnInvalidTokenWhenSignatureDoesNotMatch() throws Exception {
        SecretKey wrongKey = Keys.hmacShaKeyFor(new byte[48]);
        String forgedToken = Jwts.builder()
                .subject("someone@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(wrongKey)
                .compact();

        mockMvc.perform(get(STUDENTS_URL).header("Authorization", "Bearer " + forgedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(INVALID_TOKEN))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturnExpiredTokenMessageWhenTokenIsExpired() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String expiredToken = Jwts.builder()
                .subject("someone@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();

        mockMvc.perform(get(STUDENTS_URL).header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(EXPIRED_TOKEN))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturnInvalidCredentialsWhenLoginFails() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").value(BAD_CREDENTIALS))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnForbiddenWhenUserHitsAdminOnlyEndpoint() throws Exception {
        String token = loginAs("roleuser@example.com", "USER");

        mockMvc.perform(post(STUDENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").value(ACCESS_DENIED))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value(STUDENTS_URL));
    }

    @Test
    void shouldReturnForbiddenWhenNormalUserUpdatesStudent() throws Exception {
        String token = loginAs("patchuser@example.com", "USER");

        mockMvc.perform(patch(STUDENTS_URL + "/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Hacked\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").value(ACCESS_DENIED))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.path").value(STUDENTS_URL + "/1"));
    }

    @Test
    void shouldReturnForbiddenWhenNormalUserDeletesStudent() throws Exception {
        String token = loginAs("deleteuser@example.com", "USER");

        mockMvc.perform(delete(STUDENTS_URL + "/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").value(ACCESS_DENIED));
    }

    @Test
    void shouldLetAdminReachAdminOnlyStudentEndpoints() throws Exception {
        String token = loginAs("adminuser@example.com", "ADMIN");

        mockMvc.perform(delete(STUDENTS_URL + "/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    org.junit.jupiter.api.Assertions.assertTrue(
                            status != 401 && status != 403,
                            "ADMIN was rejected with status " + status);
                });
    }

    @Test
    void shouldAllowValidTokenOnProtectedEndpoint() throws Exception {
        String token = loginAs("validtoken@example.com", "USER");

        mockMvc.perform(get(STUDENTS_URL).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    private String loginAs(String email, String role) throws Exception {
        if (studentRepository.findByEmail(email).isEmpty()) {
            Student student = new Student();
            student.setFirstName("Auth");
            student.setLastName("Tester");
            student.setEmail(email);
            student.setPassword(passwordEncoder.encode("Password@123"));
            student.setRole(role);
            studentRepository.save(student);
        }

        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"Password@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        int start = body.indexOf("\"token\":\"") + "\"token\":\"".length();
        int end = body.indexOf('"', start);
        return body.substring(start, end);
    }
}
