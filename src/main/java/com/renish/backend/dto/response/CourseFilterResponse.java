package com.renish.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class CourseFilterResponse {
    private Long id;
    private String courseName;
    private String duration;
    private Double fees;
    private String instructorName;
    private String  departmentName; 
}
