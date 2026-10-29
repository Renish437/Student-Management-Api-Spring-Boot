package com.renish.backend.mapper;

import com.renish.backend.dto.response.DepartmentCourseSummaryResponse;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.StudentSummaryResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.entity.Department;

public class AdminDepartmentMapper {


    public static AdminDepartmentResponse toDepartmentResponse(Department department){

        if(department==null) return null;

        return AdminDepartmentResponse.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .code(department.getCode())
                .description(department.getDescription())
                .headOfDepartment(department.getHeadOfDepartment())
                .location(department.getLocation())
                .contactEmail(department.getContactEmail())
                .contactNumber(department.getContactNumber())
                .status(department.getStatus())
                .courseCount((long) department.getCourses().size())
                .courses(department.getCourses().stream().map(course -> {
                    if(course==null) return null;
                    return DepartmentCourseSummaryResponse.builder()
                            .id(course.getId())
                            .courseName(course.getCourseName())
                            .build();
                }).toList())
                .studentCount((long) department.getStudents().size())
                .students(department.getStudents().stream().map(student->{
                    if(student==null) return null;
                    return  StudentSummaryResponse.builder()
                            .id(student.getId())
                            .firstName(student.getFirstName())
                            .lastName(student.getLastName())
                            .email(student.getEmail())

                            .build();
                }).toList())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .createdBy(department.getCreatedBy())
                .lastModifiedBy(department.getLastModifiedBy())
                .version(department.getVersion())
                .deleted(department.isDeleted())
                .deletedBy(department.getDeletedBy())
                .deletedAt(department.getDeletedAt())
                .build();
    }
}
