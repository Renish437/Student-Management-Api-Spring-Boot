package com.renish.backend.dto.response;

import com.renish.backend.enums.DepartmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class DepartmentStatusResponse {
    
    private DepartmentStatus status;

}
