package com.renish.backend.service;

import com.renish.backend.dto.request.AddressRequest;
import com.renish.backend.dto.request.CourseRequest;
import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.admin.AdminCourseFilterRequest;
import com.renish.backend.dto.request.admin.AdminDepartmentFilterRequest;
import com.renish.backend.dto.request.admin.AdminStudentFilterRequest;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.dto.response.admin.AdminCourseResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import com.renish.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Soft-delete behaviour verified the way it runs in production: this class is
 * intentionally NOT annotated with @Transactional, so every service call opens
 * its own transaction like a controller request does (the aspect-based
 * {@code @IncludeDeleted} handling cannot lean on a test-managed transaction).
 */
@SpringBootTest
@ActiveProfiles("test")
class SoftDeleteFlowIntegrationTest {

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private AdminStudentService adminStudentService;
    @Autowired
    private AdminCourseService adminCourseService;
    @Autowired
    private AdminDepartmentService adminDepartmentService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long departmentId;
    private Long courseId;
    private Long studentId;

    @BeforeEach
    void setUp() {
        cleanup();

        DepartmentResponse department = departmentService.createDepartment(
                DepartmentRequest.builder()
                        .departmentName("Soft Delete Dept")
                        .code("SD")
                        .build());
        CourseResponse course = courseService.createCourse(
                CourseRequest.builder()
                        .courseName("Soft Delete Course")
                        .duration("6 Months")
                        .fees(1000.0)
                        .instructorName("Renish")
                        .departmentId(department.getId())
                        .build());

        StudentRequest request = new StudentRequest();
        request.setFirstName("Soft");
        request.setLastName("Delete");
        request.setEmail("soft.delete@sms.test");
        request.setPassword("Password1!");
        AddressRequest address = new AddressRequest();
        address.setCity("Kathmandu");
        address.setState("Bagmati");
        address.setCountry("Nepal");
        request.setAddress(address);
        request.setDepartmentId(department.getId());
        request.setCourseIds(List.of(course.getId()));
        StudentResponse student = studentService.createStudent(request);

        departmentId = department.getId();
        courseId = course.getId();
        studentId = student.getId();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    // ---------- students ----------

    @Test
    void softDeletedStudentIsHiddenFromUserApiButVisibleToAdminAndRestorable() {
        assertEquals(studentId, studentService.getStudentById(studentId).getId());

        adminStudentService.deleteBulkStudent(List.of(studentId));

        // the row survives with the soft-delete markers
        assertEquals(1, count(
                "SELECT COUNT(*) FROM students WHERE id = ? AND deleted = true AND deleted_by IS NOT NULL AND deleted_at IS NOT NULL",
                studentId));

        // user-facing API no longer sees it
        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(studentId));
        assertTrue(studentService.getAllStudents(Pageable.unpaged()).getContent().stream()
                .noneMatch(s -> studentId.equals(s.getId())));

        // admin API still sees it, flagged as deleted
        AdminStudentResponse adminView = adminStudentService.getStudentById(studentId);
        assertTrue(adminView.isDeleted());

        AdminStudentFilterRequest deletedOnly = new AdminStudentFilterRequest();
        deletedOnly.setDeleted(true);
        PageResponse<AdminStudentResponse> deletedPage =
                adminStudentService.getAllStudents(deletedOnly, PageRequest.of(0, 10));
        assertTrue(deletedPage.getContent().stream().anyMatch(s -> studentId.equals(s.getId())));

        AdminStudentFilterRequest activeOnly = new AdminStudentFilterRequest();
        activeOnly.setDeleted(false);
        PageResponse<AdminStudentResponse> activePage =
                adminStudentService.getAllStudents(activeOnly, PageRequest.of(0, 10));
        assertTrue(activePage.getContent().stream().noneMatch(s -> studentId.equals(s.getId())));

        // restore brings it back for the user API
        AdminStudentResponse restored = adminStudentService.restoreStudentById(studentId);
        assertFalse(restored.isDeleted());
        assertEquals(studentId, studentService.getStudentById(studentId).getId());
    }

    @Test
    void hardDeletePurgesSoftDeletedStudentInItsOwnTransaction() {
        adminStudentService.deleteBulkStudent(List.of(studentId));
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));

        // hard delete must be able to see the soft-deleted row
        adminStudentService.hardDeleteStudent(studentId);

        assertEquals(0, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));
    }

    // ---------- departments ----------

    @Test
    void softDeletedDepartmentIsHiddenFromUserApiButVisibleToAdminAndRestorable() {
        assertEquals(departmentId, departmentService.getDepartmentById(departmentId).getId());

        departmentService.deleteDepartment(departmentId);

        assertEquals(1, count(
                "SELECT COUNT(*) FROM departments WHERE id = ? AND deleted = true AND deleted_by IS NOT NULL AND deleted_at IS NOT NULL",
                departmentId));

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(departmentId));
        assertTrue(departmentService.getAllDepartments(Pageable.unpaged()).getContent().stream()
                .noneMatch(d -> departmentId.equals(d.getId())));

        AdminDepartmentResponse adminView = adminDepartmentService.getDepartmentById(departmentId);
        assertTrue(adminView.isDeleted());

        AdminDepartmentFilterRequest deletedOnly = new AdminDepartmentFilterRequest();
        deletedOnly.setDeleted(true);
        PageResponse<AdminDepartmentResponse> deletedPage =
                adminDepartmentService.getAllDepartments(deletedOnly, PageRequest.of(0, 10));
        assertTrue(deletedPage.getContent().stream().anyMatch(d -> departmentId.equals(d.getId())));

        AdminDepartmentFilterRequest activeOnly = new AdminDepartmentFilterRequest();
        activeOnly.setDeleted(false);
        PageResponse<AdminDepartmentResponse> activePage =
                adminDepartmentService.getAllDepartments(activeOnly, PageRequest.of(0, 10));
        assertTrue(activePage.getContent().stream().noneMatch(d -> departmentId.equals(d.getId())));

        AdminDepartmentResponse restored = adminDepartmentService.restoreDepartmentById(departmentId);
        assertFalse(restored.isDeleted());
        assertEquals(departmentId, departmentService.getDepartmentById(departmentId).getId());
    }

    // ---------- courses ----------

    @Test
    void softDeletedCourseIsHiddenFromUserApiButVisibleToAdminAndRestorable() {
        assertEquals(courseId, courseService.getCourseById(courseId).getId());

        courseService.deleteCourse(courseId);

        assertEquals(1, count(
                "SELECT COUNT(*) FROM courses WHERE id = ? AND deleted = true AND deleted_by IS NOT NULL AND deleted_at IS NOT NULL",
                courseId));

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(courseId));
        assertTrue(courseService.getAllCourses(Pageable.unpaged()).getContent().stream()
                .noneMatch(c -> courseId.equals(c.getId())));

        AdminCourseResponse adminView = adminCourseService.getCourseById(courseId);
        assertTrue(adminView.isDeleted());

        AdminCourseFilterRequest deletedOnly = new AdminCourseFilterRequest();
        deletedOnly.setDeleted(true);
        PageResponse<AdminCourseResponse> deletedPage =
                adminCourseService.getAllCourses(deletedOnly, PageRequest.of(0, 10));
        assertTrue(deletedPage.getContent().stream().anyMatch(c -> courseId.equals(c.getId())));

        AdminCourseFilterRequest activeOnly = new AdminCourseFilterRequest();
        activeOnly.setDeleted(false);
        PageResponse<AdminCourseResponse> activePage =
                adminCourseService.getAllCourses(activeOnly, PageRequest.of(0, 10));
        assertTrue(activePage.getContent().stream().noneMatch(c -> courseId.equals(c.getId())));

        AdminCourseResponse restored = adminCourseService.restoreCourseById(courseId);
        assertFalse(restored.isDeleted());
        assertEquals(courseId, courseService.getCourseById(courseId).getId());
    }

    // ---------- helpers ----------

    private void cleanup() {
        jdbcTemplate.execute("DELETE FROM student_courses");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM addresses");
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM departments");
    }

    private int count(String sql, Object... args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args);
        assertNotNull(result);
        return result.intValue();
    }
}