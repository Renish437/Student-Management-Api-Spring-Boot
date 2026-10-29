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
public class AdminStudentFilterRequest {

    @Schema(description = "Filter students by first name")
    private String firstName;

    @Schema(description = "Filter students by last name")
    private String lastName;

    @Schema(description = "Filter students by email address")
    private String email;

    @Schema(description = "Filter students by enrolled course name")
    private String courseName;

    @Schema(description = "Filter students with course fees greater than or equal to this amount")
    private Double minFee;

    @Schema(description = "Filter students with course fees less than or equal to this amount")
    private Double maxFee;

    @Schema(description = "Filter students by department name")
    private String departmentName;

    @Builder.Default
    @Schema(
            description = "Filter students by deletion status. Use ALL to include both active and deleted students",
            defaultValue = "ALL",
            example = "ALL",
            allowableValues = {"ALL", "ACTIVE", "DELETED"}
    )
    private String status = "ALL";

    @Schema(
            description = "Filter students by deletion status. True for deleted students, false for active students",
            defaultValue = "false",
            example = "false"
    )
    private boolean deleted;
}