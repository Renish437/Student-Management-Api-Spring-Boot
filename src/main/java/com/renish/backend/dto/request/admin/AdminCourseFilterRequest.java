package com.renish.backend.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCourseFilterRequest {
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

    @Schema(
            description = "Filter courses by deletion status. True for deleted course, false for active course",
            defaultValue = "false",
            example = "false"
    )
    private boolean deleted;
}
