package com.renish.backend.repository;

import com.renish.backend.dto.response.StudentProjectionResponse;
import com.renish.backend.entity.Student;
import com.renish.backend.projection.StudentProjection;

import com.renish.backend.repository.custom.StudentRepositoryCustom;
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

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student>, StudentRepositoryCustom {

        boolean existsByEmail(String email);

        boolean existsByEmailAndIdNot(String email, Long id);

        @EntityGraph(attributePaths = { "address", "department","courses"})
        // @Query("SELECT s FROM Student s")
        Page<Student> findAll(Pageable pageable);

        /**
         * Redeclared as a query on purpose: Hibernate does not apply enabled filters to
         * {@code find()}-by-id loads, so the soft-delete filter would be bypassed and
         * deleted students would still show up for the regular (non-admin) API.
         * HQL/JPQL queries do honour the filter, and the admin layer sees deleted rows
         * because {@code @IncludeDeleted} disables the filter for the whole call.
         */
        @EntityGraph(attributePaths = { "department", "address","courses" })
        @Query("SELECT s FROM Student s WHERE s.id = :id")
        Optional<Student> findById(@Param("id") Long id);


        // @Query("""
        //         SELECT DISTINCT s
        //         FROM Student s 
        //         LEFT JOIN FETCH s.courses
        //         LEFT JOIN FETCH s.department 
        //         LEFT JOIN FETCH s.address
        //         """)
        // Page<Student> findAllWithDetails(Pageable pageable);

        @Query(value = """
                        SELECT
                            s.id AS id,
                            s.firstName AS firstName,
                            s.lastName AS lastName,
                            s.email AS email
                        FROM Student s
                        JOIN s.courses c
                        GROUP BY s.id, s.firstName, s.lastName, s.email
                        HAVING SUM(c.fees) BETWEEN :minFee AND :maxFee
                        """, countQuery = """
                        SELECT COUNT(s)
                        FROM Student s
                        WHERE s.id IN (
                            SELECT s2.id
                            FROM Student s2
                            JOIN s2.courses c2
                            GROUP BY s2.id
                            HAVING SUM(c2.fees) BETWEEN :minFee AND :maxFee
                        )
                        """)
        Page<StudentProjection> findByStudentsTotalFees(
                        @Param("minFee") Double minFee,
                        @Param("maxFee") Double maxFee,
                        Pageable pageable);

        Page<StudentProjection> findBy(Pageable pageable);

        @Query("""
                        SELECT new com.renish.backend.dto.response.StudentProjectionResponse(s.firstName,s.lastName,s.email) FROM Student s
                        """)
        Page<StudentProjectionResponse> getStudentProjection(Pageable pageable);

        Optional<Student> findByEmail(String email);

        @Query("""
                        SELECT COUNT(s) FROM Student s JOIN s.courses c WHERE LOWER(c.courseName) = LOWER(:courseName)
                        """)
        Long countStudentByCourse(@Param("courseName") String courseName);

        void deleteByEmail(String email);

        @Modifying
        @Query(value = "DELETE FROM student_courses WHERE student_id IN :ids", nativeQuery = true)
        void deleteEnrollmentsByStudentIds(@Param("ids") List<Long> ids);

        @Query(value = """
                        SELECT address_id FROM students
                        WHERE id IN :ids AND address_id IS NOT NULL
                        """, nativeQuery = true)
        List<Long> findAddressIdsByStudentIds(@Param("ids") List<Long> ids);

        @Modifying
        @Query(value = "DELETE FROM addresses WHERE id IN :addressIds", nativeQuery = true)
        void deleteAddressesByIds(@Param("addressIds") List<Long> addressIds);

        @Query("""
                        SELECT s FROM Student s
                        JOIN s.courses c
                        WHERE s.firstName = :firstName
                        AND LOWER(c.courseName) = LOWER(:course)
                        """)
        List<Student> findByFirstNameAndCourse(@Param("firstName") String firstName, @Param("course") String course);

        List<Student> findByFirstNameContaining(String keyword);

        List<Student> findByFirstNameStartingWith(String keyword);

        List<Student> findByFirstNameEndingWith(String keyword);
        // List<Student> findByFirstNameIgnoreCase(String keyword);
        // List<Student> findByCoursesIn(List<String> courses);
        // List<Student> findByCoursesNotIn(List<String> courses);
        // List<Student> findByFeesLessThan(Double fees);
        // List<Student> findByFeesGreaterThan(Double fees);
        // List<Student> findByFeesBetween(Double minFees,Double maxFees);
        // List<Student> findByPhoneNumberIsNull();
        // List<Student> findByPhoneNumberIsNotNull();

        // List<Student> findByCourseOrderByFirstNameAsc(String course)
        // ;
        // List<Student> findByCourseAndFirstNameContainingIgnoreCase(String
        // course,String keyword);




        // ADMIN REPO
        @Query(
                value = "SELECT * FROM students",
                countQuery = "SELECT COUNT(*) FROM students",
                nativeQuery = true
        )
        Page<Student> findAllStudentsIncludingDeleted(Pageable pageable);

}
