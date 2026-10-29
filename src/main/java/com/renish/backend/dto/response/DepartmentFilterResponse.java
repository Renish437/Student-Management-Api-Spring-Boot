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
public class DepartmentFilterResponse {
    private Long id;

    private String departmentName;

    private String code;

    private String description;

    private String headOfDepartment;

    private String location;

    private String contactEmail;

    private String contactNumber;

    private DepartmentStatus status;


}
