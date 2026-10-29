package com.renish.backend.dto.response.admin;


import com.renish.backend.dto.response.DepartmentCourseSummaryResponse;
import com.renish.backend.dto.response.StudentSummaryResponse;
import com.renish.backend.enums.DepartmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AdminDepartmentResponse extends AdminBaseResponse {
    private Long id;

    private String departmentName;

    private String code;

    private String description;

    private String headOfDepartment;

    private String location;

    private String contactEmail;

    private String contactNumber;

    private DepartmentStatus status;

    private List<DepartmentCourseSummaryResponse> courses;

    private Long courseCount;

    private List<StudentSummaryResponse> students;

    private Long studentCount;



}