package com.renish.backend.service;

import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.admin.AdminStudentFilterRequest;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminStudentService {

    PageResponse<AdminStudentResponse> getAllStudents(AdminStudentFilterRequest req, Pageable pageable);
    List<StudentResponse> createBulkStudent(List<StudentRequest> req);
    void deleteBulkStudent(List<Long> ids);
    AdminStudentResponse getStudentById(Long id);
    AdminStudentResponse restoreStudentById(Long id);

    void hardDeleteStudent(Long id);
    void bulkHardDeleteStudent(List<Long> ids);

}
