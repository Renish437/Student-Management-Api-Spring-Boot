package com.renish.backend.dto.request;

import com.renish.backend.enums.CourseStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data 
@AllArgsConstructor 
@RequiredArgsConstructor 
@Builder 
public class CourseFilterRequest {

    @Schema(description = "Filter by course name")
    private String courseName;

    @Schema(description = "Filter by course duration")
    private String duration;

    @Schema(description = "Filter by instructor name")
    private String instructorName;

    @Schema(description = "Filter by department name")
    private String departmentName;

    @Schema(description = "Filter courses with fees greater than or equal to this amount")
    private Double minFee;

    @Schema(description = "Filter courses with fees less than or equal to this amount")
    private Double maxFee;


    @Builder.Default
    @Schema(description = "Filter by course status. Use ALL to include all statuses",defaultValue = "ALL",example = "ALL")
    private String status = "ALL";
}
