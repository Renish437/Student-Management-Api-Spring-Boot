package com.renish.backend.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data 
@AllArgsConstructor 
@RequiredArgsConstructor 
@Builder 
public class DepartmentFilterRequest {
    @Parameter(
            description = "Search by department name or code"
    )
    private String keyword;   // departmentName,code

    @Parameter(
            description = "Search by contact email and phone"
    )
    private String contact;   // contactEmail, contactPhone

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
            description = "Filter by description status. Use ALL to include every status",
            example = "ALL",
            defaultValue = "ALL"
    )
    private String status="ALL";


}
