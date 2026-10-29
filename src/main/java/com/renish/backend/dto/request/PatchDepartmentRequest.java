package com.renish.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchDepartmentRequest {
    @Size(min = 1, max = 100,
            message = "Department name must be between 1 and 100 characters")
    private String departmentName;

    private String code;

    private String description;

    private String headOfDepartment;

    private String location;

    @Email(message = "Please enter a valid department contact email")
    private String contactEmail;

    private String contactNumber;


    @NotNull(message = "Department version is required to prevent conflicting updates")
    @PositiveOrZero(message = "Department version cannot be negative")
    private Long version;

}
