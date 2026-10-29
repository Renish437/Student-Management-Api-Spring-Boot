package com.renish.backend.mapper;

import com.renish.backend.dto.response.AddressResponse;
import com.renish.backend.dto.response.CourseSummaryResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.entity.Address;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;

import java.util.Optional;

public class StudentMapper {
    public static StudentResponse toStudentResponse(Student student) {
        if (student == null) {
            return null;
        }
        Address address = student.getAddress();
        AddressResponse addressResponse = address == null ? null
                : AddressResponse.builder()
                        .city(address.getCity())
                        .state(address.getState())
                        .country(address.getCountry())
                        .build();
        Department department = student.getDepartment();

        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getFirstName() + " " + student.getLastName())
                .email(student.getEmail())
                .profileImage(student.getProfileImage())
                .address(addressResponse)
                .departmentName(Optional.ofNullable(department).map(Department::getDepartmentName).orElse(null))
                .courses(student.getCourses().stream().map(course -> {
                    if(course==null) return null;

                    return CourseSummaryResponse.builder()
                            .id(course.getId())
                            .courseName(course.getCourseName())
                            .duration(course.getDuration())
                            .fees(course.getFees())
                            .instructorName(course.getInstructorName())
                            .build();
                        }).toList()
                )
                // .password(student.getPassword())
                 .createdAt(student.getCreatedAt())
                 .updatedAt(student.getUpdatedAt())
                .createdBy(student.getCreatedBy())
                .lastModifiedBy(student.getLastModifiedBy())
                .version(student.getVersion())
                .build();
    }


}
