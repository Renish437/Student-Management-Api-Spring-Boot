package com.renish.backend.repository;

import com.renish.backend.entity.Course;

import com.renish.backend.repository.custom.CourseRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Long>,JpaSpecificationExecutor<Course>, CourseRepositoryCustom {
    boolean existsByCourseName(String courseName);
    boolean existsByCourseNameAndDepartmentId(
            String courseName, Long departmentId);

    boolean existsByCourseNameAndDepartmentIdAndIdNot(
            String courseName, Long departmentId, Long id);

    @EntityGraph(attributePaths = {"students","department"})
    Page<Course> findAll(Pageable pageable);

    /** Query-based on purpose: find()-by-id bypasses the soft-delete filter. */
    @EntityGraph(attributePaths = {"students","department"})
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findById(@Param("id") Long id);


    @EntityGraph(attributePaths = {"department"})
    @Query("SELECT c FROM Course c")
    List<Course> findAllWithDepartment();

    @Modifying
    @Query(value = "DELETE FROM student_courses WHERE course_id IN :ids", nativeQuery = true)
    void deleteEnrollmentsByCourseIds(@Param("ids") List<Long> ids);

}
