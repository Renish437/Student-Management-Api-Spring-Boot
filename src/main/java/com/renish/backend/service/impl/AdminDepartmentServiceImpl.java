package com.renish.backend.service.impl;

import com.renish.backend.annotation.IncludeDeleted;
import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.UpdateDepartmentStatusRequest;
import com.renish.backend.dto.request.admin.AdminDepartmentFilterRequest;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.ConflictException;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.AdminDepartmentMapper;
import com.renish.backend.mapper.DepartmentMapper;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.service.AdminDepartmentService;
import com.renish.backend.specification.AdminDepartmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDepartmentServiceImpl implements AdminDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final AuditorAware<String> auditorAware;


    @Override
    @Transactional(readOnly = true)
    @IncludeDeleted
    @Cacheable(
            value = "adminDepartmentsPage",
            key = "#req.keyword + ':' + #req.contact + ':' + #req.headDepartment + ':' + #req.location + ':' + #req.status + ':' + #req.deleted + ':' + #pageable.toString()"
    )
    public PageResponse<AdminDepartmentResponse> getAllDepartments(AdminDepartmentFilterRequest req, Pageable pageable) {
        Specification<Department> spec = AdminDepartmentSpecification.filterDepartment(req.getKeyword(),req.getContact(),req.getHeadDepartment(),req.getLocation(),req.getStatus(), req.isDeleted());
        return PageResponse.of(departmentRepository.findAll(spec,pageable).map(AdminDepartmentMapper::toDepartmentResponse));
    }

    @Transactional
    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
    @Override
    public List<DepartmentResponse> createBulkDepartment(List<DepartmentRequest> req) {
        List<Department> departments = departmentRepository.saveAll(req.stream().map(this::toDepartment).toList());
        return departments.stream().map(DepartmentMapper::toDepartmentResponse).toList();
    }

    @Transactional
    @Caching(
            evict = {@CacheEvict(value = {"departments", "adminDepartments"}, key = "#id"),
                    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
            }
    )
    public DepartmentStatusResponse updateDepartmentStatus(UpdateDepartmentStatusRequest req, Long id) {
        Department department = getEntityById(id);
        department.setStatus(req.getStatus());
        return DepartmentMapper.toDepartmentStatusResponse(department);
    }
    @Transactional
    @CacheEvict(value = {"departments", "adminDepartments", "departmentsPage", "adminDepartmentsPage"}, allEntries = true)
    @Override
    public void deleteBulkDepartment(List<Long> ids) {
        List<Department> departments = getEntitiesByIds(ids);
        String deleteByUser = auditorAware.getCurrentAuditor().orElse("admin");
        departments.forEach(d->d.markAsDeleted(deleteByUser));
    }


    @IncludeDeleted
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "adminDepartments", key = "#id")
    public AdminDepartmentResponse getDepartmentById(Long id) {
      Department department = getEntityById(id);
      return AdminDepartmentMapper.toDepartmentResponse(department);
    }

    @IncludeDeleted
    @Transactional
    @Caching(
            evict = {@CacheEvict(value = {"departments", "adminDepartments"}, key = "#id"),
                    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
            }
    )
    @Override
    public AdminDepartmentResponse restoreDepartmentById(Long id) {
        Department department = getEntityById(id);
        if(!department.isDeleted()){
            return AdminDepartmentMapper.toDepartmentResponse(department);
        }
        String restoredByUser = auditorAware.getCurrentAuditor().orElse("admin");
        department.restoreDeleted(restoredByUser);
        return AdminDepartmentMapper.toDepartmentResponse(department);
    }

    @Transactional
    @IncludeDeleted
    @CacheEvict(value = {"departments", "adminDepartments", "departmentsPage", "adminDepartmentsPage"}, allEntries = true)
    @Override
    public void hardDeleteDepartment(Long id) {
        bulkHardDeleteDepartment(List.of(id));
    }

    @Transactional
    @IncludeDeleted
    @CacheEvict(value = {"departments", "adminDepartments", "departmentsPage", "adminDepartmentsPage"}, allEntries = true)
    @Override
    public void bulkHardDeleteDepartment(List<Long> ids) {
        List<Department> departments = getEntitiesByIds(ids);
        if (departments.isEmpty()) {
            return;
        }
        assertNoDependentRecords(departments);
        departmentRepository.deleteAllInBatch(departments);
    }

    /**
     * Courses and students keep a foreign key to their department, so the delete
     * is rejected up front instead of blowing up with a 500 on the constraint.
     * Counts are done with native queries because the soft-delete filter would
     * otherwise hide rows that still reference the department.
     */
    private void assertNoDependentRecords(List<Department> departments) {
        List<Long> departmentIds = departments.stream().map(Department::getId).toList();
        long courseCount = departmentRepository.countCoursesByDepartmentIds(departmentIds);
        long studentCount = departmentRepository.countStudentsByDepartmentIds(departmentIds);

        if (courseCount > 0 || studentCount > 0) {
            String names = String.join(", ",
                    departments.stream().map(Department::getDepartmentName).toList());
            throw new ConflictException(
                    "Cannot delete department(s) [" + names + "]: " + courseCount
                            + " course(s) and " + studentCount
                            + " student(s) still reference them");
        }
    }

    public Department getEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id:" + id));
    }
    private List<Department> getEntitiesByIds(List<Long> ids) {
        List<Department> departments = departmentRepository.findAllById(ids);

        if (departments.size() != ids.stream().distinct().count()) {
            throw new ResourceNotFoundException("One or more departments not found");
        }

        return departments;
    }
    private Department toDepartment(DepartmentRequest req) {
        if (departmentRepository.existsByDepartmentName(req.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists with name: " + req.getDepartmentName()
            );
        }

        return Department.builder()
                .departmentName(req.getDepartmentName())
                .code(req.getCode())
                .description(req.getDescription())
                .headOfDepartment(req.getHeadOfDepartment())
                .contactEmail(req.getContactEmail())
                .contactNumber(req.getContactNumber())
                .location(req.getLocation())
                .build();
    }
}
