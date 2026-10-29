package com.renish.backend.dto.request.admin;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDepartmentFilterRequest {

    @Parameter(
            description = "Search by department name or code"
    )
    private String keyword;

    @Parameter(
            description = "Search by contact email or phone number"
    )
    private String contact;

    @Parameter(
            description = "Filter by head of department name",
            example = "Ram Sharma"
    )
    private String headDepartment;

    @Parameter(
            description = "Filter by department location"
    )
    private String location;

    @Builder.Default
    @Schema(
            description = "Filter by department status. Use ALL to include every status",
            example = "ALL",
            defaultValue = "ALL"
    )
    private String status = "ALL";

    @Schema(
            description = "Filter by deletion status. True for deleted departments, false for active departments",
            example = "false",
            defaultValue = "false"
    )
    private boolean deleted;
}