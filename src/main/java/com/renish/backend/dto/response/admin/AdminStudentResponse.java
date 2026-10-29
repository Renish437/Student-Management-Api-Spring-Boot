package com.renish.backend.dto.response.admin;

import com.renish.backend.dto.response.AddressResponse;
import com.renish.backend.dto.response.BaseResponse;
import com.renish.backend.dto.response.CourseSummaryResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AdminStudentResponse extends AdminBaseResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String profileImage;
    private AddressResponse address;
    private String departmentName;
    private List<CourseSummaryResponse> courses;

}

