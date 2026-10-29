package com.renish.backend.service.impl;

import java.util.List;
import java.util.Objects;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.DepartmentFilterResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.specification.DepartmentSpecification;
import jakarta.persistence.OptimisticLockException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.entity.Department;
import com.renish.backend.enums.DepartmentStatus;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.DepartmentMapper;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
   private final AuditorAware<String> auditorAware;

    @Transactional
    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
    public DepartmentResponse createDepartment(DepartmentRequest req) {

        return toDepartmentResponse(
                departmentRepository.save(toDepartment(req))
        );
    }


    @Cacheable(value = "departmentsPage",key = "#pageable.toString()")
    public PageResponse<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return PageResponse.of(
                departmentRepository
                        .findAll(pageable)
                        .map(this::toDepartmentResponse)
        );
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "adminDepartments", key = "#id"),
                    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
            },
            put  ={@CachePut(value = "departments",key = "#id")}
    )
    public DepartmentResponse updateDepartment(UpdateDepartmentRequest req, Long id) {
        Department existingDepartment = getEntityById(id);
        if (!Objects.equals(existingDepartment.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Department has been modified by another user. " +
                            "Current version is: " + existingDepartment.getVersion() +
                            ". Please refresh and try again."
            );
        }
        if (departmentRepository.existsByDepartmentName(req.getDepartmentName())
                && !Objects.equals(existingDepartment.getDepartmentName(), req.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists with this name: " + req.getDepartmentName());
        }
        existingDepartment.setDepartmentName(req.getDepartmentName());
        existingDepartment.setCode(req.getCode());
        existingDepartment.setDescription(req.getDescription());
        existingDepartment.setHeadOfDepartment(req.getHeadOfDepartment());
        existingDepartment.setContactEmail(req.getContactEmail());
        existingDepartment.setContactNumber(req.getContactNumber());
        existingDepartment.setLocation(req.getLocation());

        return toDepartmentResponse(existingDepartment);

    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "adminDepartments", key = "#id"),
                    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
            },
            put  ={@CachePut(value = "departments",key = "#id")}
    )
    public DepartmentResponse patchDepartment(PatchDepartmentRequest req, Long id) {
        Department existingDepartment = getEntityById(id);
        if (!Objects.equals(existingDepartment.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Department has been modified by another user. " +
                            "Current version is: " + existingDepartment.getVersion() +
                            ". Please refresh and try again."
            );
        }

        if (departmentRepository.existsByDepartmentName(req.getDepartmentName())
                && !Objects.equals(existingDepartment.getDepartmentName(), req.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists with this name: " + req.getDepartmentName());
        }

        if (req.getDepartmentName() != null) {
            existingDepartment.setDepartmentName(req.getDepartmentName());
        }
        if (req.getCode() != null) {
            existingDepartment.setCode(req.getCode());
        }
        if (req.getDescription() != null) {
            existingDepartment.setDescription(req.getDescription());
        }
        if (req.getHeadOfDepartment() != null) {
            existingDepartment.setHeadOfDepartment(req.getHeadOfDepartment());
        }
        if (req.getContactEmail() != null) {
            existingDepartment.setContactEmail(req.getContactEmail());
        }
        if (req.getContactNumber() != null) {
            existingDepartment.setContactNumber(req.getContactNumber());
        }
        if (req.getLocation() != null) {
            existingDepartment.setLocation(req.getLocation());
        }
        return toDepartmentResponse(existingDepartment);
    }



    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "departments",key = "#id"),
                    @CacheEvict(value = "adminDepartments", key = "#id"),
                    @CacheEvict(value = {"departmentsPage", "adminDepartmentsPage"}, allEntries = true)
            }
    )
    public void deleteDepartment(Long id) {
        Department existingDepartment = getEntityById(id);
        String deleteByUser = auditorAware.getCurrentAuditor().orElse("admin");
        existingDepartment.markAsDeleted(deleteByUser);
    }


    @Override
    public PageResponse<DepartmentFilterResponse> filterDepartment(DepartmentFilterRequest req, Pageable pageable) {
        Specification<Department> spec = DepartmentSpecification.filterDepartment(
                req.getKeyword(),
                req.getContact(),
                req.getHeadDepartment(),
                req.getLocation(),
                req.getStatus()
        );
        Page<Department> departments = departmentRepository.findAll(spec,pageable);
        return PageResponse.of(departments.map(DepartmentMapper::toDepartmentFilterResponse));
    }

    @Cacheable(value = "departments",key = "#id")
    public DepartmentResponse getDepartmentById(Long id) {
        return toDepartmentResponse(getEntityById(id));
    }


    public Department getEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id:" + id));
    }

    private DepartmentResponse toDepartmentResponse(Department department) {
        return DepartmentMapper.toDepartmentResponse(
                department
        );
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
