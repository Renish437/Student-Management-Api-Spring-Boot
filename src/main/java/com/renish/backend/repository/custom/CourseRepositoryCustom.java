package com.renish.backend.repository.custom;

import com.renish.backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CourseRepositoryCustom {
    Page<Course> findAllWithDepartment(
            Specification<Course> specification,
            Pageable pageable);
}
