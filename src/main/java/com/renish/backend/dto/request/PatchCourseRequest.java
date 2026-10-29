package com.renish.backend.dto.request;

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
public class PatchCourseRequest {
    @Size(min = 1, max = 100,
            message = "Course name must contain between 1 and 100 characters")
    private String courseName;

    private String duration;

    @PositiveOrZero(message = "Course fees must be zero or greater")
    private Double fees;

    private String instructorName;

    private Long departmentId;

    @NotNull(message = "Course version is required to prevent conflicting updates")
    @PositiveOrZero(message = "Course version cannot be negative")
    private Long version;
}
