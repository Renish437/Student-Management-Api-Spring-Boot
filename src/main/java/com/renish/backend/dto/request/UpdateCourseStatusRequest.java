package com.renish.backend.dto.request;

import com.renish.backend.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCourseStatusRequest {

    @NotBlank(message = "Course status is required")
    private CourseStatus status;
}
