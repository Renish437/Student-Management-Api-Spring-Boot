package com.renish.backend.dto.response.admin;

import com.renish.backend.dto.response.BaseResponse;
import com.renish.backend.dto.response.StudentSummaryResponse;
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
public class AdminCourseResponse extends AdminBaseResponse {
    private Long id;
    private String courseName;
    private String duration;
    private Double fees;
    private String instructorName;
    private String  departmentName;
    private List<StudentSummaryResponse> students;
    private Long studentCount;


}