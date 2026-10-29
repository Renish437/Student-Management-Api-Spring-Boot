package com.renish.backend.dto.request;


import com.renish.backend.enums.DepartmentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data 
public class UpdateDepartmentStatusRequest {
    
    @NotNull(message = "Department status is required")
    private DepartmentStatus status;
}
