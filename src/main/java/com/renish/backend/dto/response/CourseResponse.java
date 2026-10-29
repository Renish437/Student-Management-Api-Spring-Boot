package com.renish.backend.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CourseResponse extends BaseResponse {
    private Long id;
    private String courseName;
    private String duration;
    private Double fees;
    private String instructorName;
    private String  departmentName;
    private List<StudentSummaryResponse> students;
    private Long studentCount;


}
