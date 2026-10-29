package com.renish.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStudentRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @Valid
    private AddressRequest address;

    private Long departmentId;

    private List<@NotNull(message = "Course ID cannot be null") Long> courseIds;

    @NotNull(message = "Student version is required to prevent conflicting updates")
    @PositiveOrZero(message = "Student version cannot be negative")
    private Long version;
}
