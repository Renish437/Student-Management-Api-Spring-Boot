package com.renish.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateCourseRequest {
    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String courseName;

    @NotBlank(message = "Course duration is required")
    private String duration;

    @NotNull(message = "Course fees are required")
    @PositiveOrZero(message = "Course fees must be zero or greater")
    private Double fees;

    @NotBlank(message = "Instructor name is required")
    private String instructorName;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Course version is required to prevent conflicting updates")
    @PositiveOrZero(message = "Course version cannot be negative")
    private Long version;
}
