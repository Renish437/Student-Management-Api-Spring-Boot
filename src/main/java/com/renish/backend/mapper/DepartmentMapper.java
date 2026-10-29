package com.renish.backend.mapper;

import com.renish.backend.dto.response.*;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.repository.DepartmentRepository;

public class DepartmentMapper {


    public static DepartmentResponse toDepartmentResponse(Department department){
    
        if(department==null) return null;

        return DepartmentResponse.builder()
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
        .build();
    }

    public static  DepartmentStatusResponse toDepartmentStatusResponse(Department department){
        if(department==null) return null;
        return DepartmentStatusResponse.builder()
        .status(department.getStatus())

        .build();
    }
    public static StudentSummaryResponse toDepartmentStudentResponse(Student student){
        if(student==null) return null;
        return  StudentSummaryResponse.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .build();
    }
    public static DepartmentFilterResponse toDepartmentFilterResponse(Department department){
        if(department==null) return null;
        return DepartmentFilterResponse.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .code(department.getCode())
                .description(department.getDescription())
                .headOfDepartment(department.getHeadOfDepartment())
                .location(department.getLocation())
                .contactEmail(department.getContactEmail())
                .contactNumber(department.getContactNumber())
                .status(department.getStatus())
                .build();
    }

}
