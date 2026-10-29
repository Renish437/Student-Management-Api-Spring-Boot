package com.renish.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter 
@AllArgsConstructor
public class StudentProjectionResponse {
    
    private String firstName;
    private String lastName;
    private String email;
}
