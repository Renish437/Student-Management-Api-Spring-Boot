package com.renish.backend.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class StudentResponse extends BaseResponse {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private AddressResponse address;
     private String departmentName;
     private List<CourseSummaryResponse> courses;


    // private String password;

}
