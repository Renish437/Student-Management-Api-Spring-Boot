package com.renish.backend.dto.response;

import com.renish.backend.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseStatusResponse {


    private CourseStatus status;
}
