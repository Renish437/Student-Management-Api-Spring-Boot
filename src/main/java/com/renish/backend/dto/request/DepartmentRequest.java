package com.renish.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100,message = "Department name cannot exceed 100 characters")
    private String departmentName;

    private String code;

    private String description;

    private String headOfDepartment;

    private String location;

    private String contactEmail;

    private String contactNumber;

   

    
}
