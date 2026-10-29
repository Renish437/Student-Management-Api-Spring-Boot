package com.renish.backend.dto.response;

import com.renish.backend.enums.DepartmentStatus;


import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DepartmentResponse extends BaseResponse {
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
