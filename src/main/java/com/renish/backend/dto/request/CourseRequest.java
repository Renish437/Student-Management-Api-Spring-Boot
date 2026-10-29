package com.renish.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRequest {
    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Duration is required")
    private String duration;

    @NotNull(message = "Fees is required")
    private Double fees;

    @NotBlank(message = "Instructor name is required")
    private String instructorName;

    @NotNull(message = "Department Id is required")
    private Long departmentId;

}
