package com.renish.backend.repository.custom;

import com.renish.backend.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface StudentRepositoryCustom {
    List<Student> findStudentCustom();
    List<Student> findStudentByDepartment(String departmentName);
    public Page<Student> findAllWithRelations(
            Specification<Student> specification,
            Pageable pageable);
}
