package com.renish.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSummaryResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;


}
