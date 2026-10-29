package com.renish.backend.service;

import com.renish.backend.entity.Address;
import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.ConflictException;
import com.renish.backend.repository.CourseRepository;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminHardDeleteIntegrationTest {

    @Autowired
    private AdminStudentService adminStudentService;

    @Autowired
    private AdminCourseService adminCourseService;

    @Autowired
    private AdminDepartmentService adminDepartmentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    // ---------- students ----------

    @Test
    void hardDeleteStudentRemovesStudentEnrollmentsAndAddress() {
        Department department = createDepartment("Dept Hard One");
        Course course = createCourse("Spring Boot", department);
        Student student = createStudent("Pooja", "pooja1@gmail.com", department, course);
        Long studentId = student.getId();
        Long addressId = student.getAddress().getId();
        flush();

        assertEquals(1, count("SELECT COUNT(*) FROM student_courses WHERE student_id = ?", studentId));
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));

        adminStudentService.hardDeleteStudent(studentId);
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));
        assertEquals(0, count("SELECT COUNT(*) FROM student_courses WHERE student_id = ?", studentId));
        assertEquals(0, count("SELECT COUNT(*) FROM addresses WHERE id = ?", addressId));
        assertEquals(1, count("SELECT COUNT(*) FROM courses WHERE id = ?", course.getId()));
    }

    @Test
    void bulkHardDeleteStudentsRemovesStudentsEnrollmentsAndAddresses() {
        Department department = createDepartment("Dept Hard Bulk");
        Course course = createCourse("Hibernate", department);
        Student first = createStudent("Pooja", "pooja2@gmail.com", department, course);
        Student second = createStudent("Ram", "ram2@gmail.com", department, course);
        List<Long> ids = List.of(first.getId(), second.getId());

        adminStudentService.bulkHardDeleteStudent(ids);
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM students WHERE id IN (?, ?)", ids.get(0), ids.get(1)));
        assertEquals(0, count("SELECT COUNT(*) FROM student_courses WHERE student_id IN (?, ?)", ids.get(0), ids.get(1)));
        assertEquals(0, count("SELECT COUNT(*) FROM addresses WHERE id IN (?, ?)",
                first.getAddress().getId(), second.getAddress().getId()));
        // the course itself survives, only the links are removed
        assertEquals(1, count("SELECT COUNT(*) FROM courses WHERE id = ?", course.getId()));
    }

    @Test
    void hardDeleteStudentPurgesSoftDeletedStudent() {
        Department department = createDepartment("Dept Purge");
        Student student = createStudent("Pooja", "pooja3@gmail.com", department, null);
        Long studentId = student.getId();

        adminStudentService.deleteBulkStudent(List.of(studentId));
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));

        adminStudentService.hardDeleteStudent(studentId);
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));
    }

    @Test
    void hardDeleteStudentThrowsWhenStudentIsMissing() {
        assertThrows(com.renish.backend.exception.ResourceNotFoundException.class,
                () -> adminStudentService.hardDeleteStudent(999999L));
        assertThrows(com.renish.backend.exception.ResourceNotFoundException.class,
                () -> adminStudentService.bulkHardDeleteStudent(List.of(999999L)));
    }

    // ---------- courses ----------

    @Test
    void hardDeleteCourseRemovesCourseAndEnrollmentRows() {
        Department department = createDepartment("Dept Course One");
        Course course = createCourse("Spring Data", department);
        Student student = createStudent("Pooja", "pooja4@gmail.com", department, course);
        Long courseId = course.getId();
        Long studentId = student.getId();
        flush();

        assertEquals(1, count("SELECT COUNT(*) FROM student_courses WHERE course_id = ?", courseId));

        adminCourseService.hardDeleteCourse(courseId);
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM courses WHERE id = ?", courseId));
        assertEquals(0, count("SELECT COUNT(*) FROM student_courses WHERE course_id = ?", courseId));
        // the student survives, only the links are removed
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", studentId));
    }

    @Test
    void bulkHardDeleteCoursesRemovesCoursesAndEnrollmentRows() {
        Department department = createDepartment("Dept Course Bulk");
        Course first = createCourse("Security", department);
        Course second = createCourse("Kafka", department);
        Student student = createStudent("Pooja", "pooja5@gmail.com", department, first);
        student.getCourses().add(second);
        studentRepository.save(student);
        flush();
        List<Long> ids = List.of(first.getId(), second.getId());

        adminCourseService.bulkHardDeleteCourse(ids);
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM courses WHERE id IN (?, ?)", ids.get(0), ids.get(1)));
        assertEquals(0, count("SELECT COUNT(*) FROM student_courses WHERE course_id IN (?, ?)", ids.get(0), ids.get(1)));
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", student.getId()));
    }

    // ---------- departments ----------

    @Test
    void hardDeleteEmptyDepartmentRemovesTheRow() {
        Department department = createDepartment("Dept Empty");

        adminDepartmentService.hardDeleteDepartment(department.getId());
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM departments WHERE id = ?", department.getId()));
    }

    @Test
    void bulkHardDeleteEmptyDepartmentsRemovesTheRows() {
        Department first = createDepartment("Dept Empty A");
        Department second = createDepartment("Dept Empty B");

        adminDepartmentService.bulkHardDeleteDepartment(List.of(first.getId(), second.getId()));
        flush();

        assertEquals(0, count("SELECT COUNT(*) FROM departments WHERE id IN (?, ?)",
                first.getId(), second.getId()));
    }

    @Test
    void hardDeleteDepartmentWithChildrenIsRejected() {
        Department department = createDepartment("Dept With Children");
        Course course = createCourse("Blocked Course", department);
        Student student = createStudent("Pooja", "pooja6@gmail.com", department, course);

        assertThrows(ConflictException.class,
                () -> adminDepartmentService.hardDeleteDepartment(department.getId()));

        assertEquals(1, count("SELECT COUNT(*) FROM departments WHERE id = ?", department.getId()));
        assertEquals(1, count("SELECT COUNT(*) FROM courses WHERE id = ?", course.getId()));
        assertEquals(1, count("SELECT COUNT(*) FROM students WHERE id = ?", student.getId()));
    }

    @Test
    void bulkHardDeleteDepartmentWithChildrenIsRejected() {
        Department withChild = createDepartment("Dept Bulk Blocked");
        Course course = createCourse("Blocked Course 2", withChild);
        createStudent("Pooja", "pooja7@gmail.com", withChild, course);
        Department withoutChild = createDepartment("Dept Bulk Free");

        assertThrows(ConflictException.class,
                () -> adminDepartmentService.bulkHardDeleteDepartment(
                        List.of(withChild.getId(), withoutChild.getId())));

        // nothing is deleted when the batch is rejected
        assertEquals(2, count("SELECT COUNT(*) FROM departments WHERE id IN (?, ?)",
                withChild.getId(), withoutChild.getId()));
        assertEquals(1, count("SELECT COUNT(*) FROM courses WHERE id = ?", course.getId()));
    }

    // ---------- helpers ----------

    private Department createDepartment(String name) {
        Department department = new Department();
        department.setDepartmentName(name);
        return departmentRepository.save(department);
    }

    private Course createCourse(String name, Department department) {
        Course course = new Course();
        course.setCourseName(name);
        course.setDuration("6 Months");
        course.setFees(25000.0);
        course.setInstructorName("Renish");
        course.setDepartment(department);
        return courseRepository.save(course);
    }

    private Student createStudent(String firstName, String email, Department department, Course course) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName("Ram");
        student.setEmail(email);
        student.setPassword("Pooja@123");
        student.setAddress(Address.builder()
                .city("Kathmandu")
                .state("Bagmati")
                .country("Nepal")
                .build());
        student.setDepartment(department);
        student.setCourses(course == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(List.of(course)));
        return studentRepository.save(student);
    }

    private void flush() {
        entityManager.flush();
    }

    private int count(String sql, Object... args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args);
        return result == null ? 0 : result.intValue();
    }
}
