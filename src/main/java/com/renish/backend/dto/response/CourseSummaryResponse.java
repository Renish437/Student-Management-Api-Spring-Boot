package com.renish.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSummaryResponse {
    private Long id;
    private String courseName;
    private String duration;
    private Double fees;
    private String instructorName;

}
