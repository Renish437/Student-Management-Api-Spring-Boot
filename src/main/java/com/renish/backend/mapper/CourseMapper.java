package com.renish.backend.mapper;

import com.renish.backend.dto.response.CourseFilterResponse;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.StudentSummaryResponse;
import com.renish.backend.entity.Course;

public class CourseMapper {

    public static CourseResponse toCourseResponse(Course course){
        if(course==null) return null;
        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .duration(course.getDuration())
                .fees(course.getFees())
                .instructorName(course.getInstructorName())
                .departmentName(course.getDepartment().getDepartmentName())
                .studentCount((long) course.getStudents().size())
                .students(course.getStudents().stream().map(student->{
                    if(student==null) return null;
                    return StudentSummaryResponse.
                            builder()
                            .id(student.getId())
                            .firstName(student.getFirstName())
                            .lastName(student.getLastName())
                            .email(student.getEmail())
                            .build();
                }).toList())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .createdBy(course.getCreatedBy())
                .lastModifiedBy(course.getLastModifiedBy())
                .version(course.getVersion())
                .build();
    }

    public static CourseStatusResponse toCourseStatusResponse(Course course){
        if(course==null) return null;
        return CourseStatusResponse.builder()
                .status(course.getStatus())
                .build();
    }
    public static  CourseFilterResponse toCourseFilterResponse(Course course){
             if(course==null) return null;

             return CourseFilterResponse.builder()
              .id(course.getId())
                .courseName(course.getCourseName())
                .duration(course.getDuration())
                .fees(course.getFees())
                .instructorName(course.getInstructorName())
                .departmentName(course.getDepartment().getDepartmentName())
             .build();
    }
}
