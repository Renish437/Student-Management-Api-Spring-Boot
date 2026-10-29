package com.renish.backend.dto.request;


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
public class PatchStudentRequest {

    @Size(min = 2, max = 30, message = "First name must be between 2 and 30")
    private String firstName;

    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30")
    private String lastName;

    @Email(message = "Please enter a valid email")
    private String email;

    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$", message = "Password must contain at least 8 characters, one uppercase, one lowercase,one number and one special character.")
    private String password;

    private AddressRequest address;

    private Long departmentId;


    private List<@NotNull Long> courseIds;

    @NotNull(message = "Student version is required to prevent conflicting updates")
    @PositiveOrZero(message = "Student version cannot be negative")
    private Long version;

}
