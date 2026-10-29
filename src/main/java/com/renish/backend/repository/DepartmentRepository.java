package com.renish.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.renish.backend.entity.Department;
import com.renish.backend.enums.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long>, JpaSpecificationExecutor<Department> {

    boolean existsByDepartmentName(String departmentName);

    Page<Department> findByStatus(DepartmentStatus status,Pageable pageable);

    @EntityGraph(attributePaths = {"students","courses"})
    Page<Department> findAll(Pageable pageable);

    /** Query-based on purpose: find()-by-id bypasses the soft-delete filter. */
    @EntityGraph(attributePaths = {"students","courses"})
    @Query("SELECT d FROM Department d WHERE d.id = :id")
    Optional<Department> findById(@Param("id") Long id);

    /** Native so the soft-delete filter cannot hide rows that still hold a foreign key. */
    @Query(value = "SELECT COUNT(*) FROM courses WHERE department_id IN :ids", nativeQuery = true)
    long countCoursesByDepartmentIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT COUNT(*) FROM students WHERE department_id IN :ids", nativeQuery = true)
    long countStudentsByDepartmentIds(@Param("ids") List<Long> ids);


//    @Query("SELECT COUNT(s) FROM Student s WHERE s.department.id = :departmentId")
//    long countByDepartmentId(@Param("departmentId") Long departmentId);
}
