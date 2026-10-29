package com.renish.backend.service;

import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.UpdateDepartmentStatusRequest;
import com.renish.backend.dto.request.admin.AdminDepartmentFilterRequest;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import com.renish.backend.entity.Department;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminDepartmentService {

    PageResponse<AdminDepartmentResponse> getAllDepartments(AdminDepartmentFilterRequest req, Pageable pageable);
    List<DepartmentResponse> createBulkDepartment(List<DepartmentRequest> req);

   DepartmentStatusResponse updateDepartmentStatus(UpdateDepartmentStatusRequest req, Long id);
    void deleteBulkDepartment(List<Long> ids);
    AdminDepartmentResponse getDepartmentById(Long id);
    AdminDepartmentResponse restoreDepartmentById(Long id);
    void hardDeleteDepartment(Long id);
    void bulkHardDeleteDepartment(List<Long> ids);
   Department getEntityById(Long id);
}
