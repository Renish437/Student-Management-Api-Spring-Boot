package com.renish.backend.service;

import java.util.List;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.DepartmentFilterResponse;
import com.renish.backend.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.entity.Department;


public interface DepartmentService {

    public DepartmentResponse createDepartment(DepartmentRequest req);

    public PageResponse<DepartmentResponse> getAllDepartments(Pageable pageable);

    public DepartmentResponse updateDepartment(UpdateDepartmentRequest req, Long id);


    public void deleteDepartment(Long id);


    public DepartmentResponse getDepartmentById(Long id);

    public DepartmentResponse patchDepartment(PatchDepartmentRequest req, Long id);


    PageResponse<DepartmentFilterResponse> filterDepartment(DepartmentFilterRequest req, Pageable pageable);
    Department getEntityById(Long id);
}
