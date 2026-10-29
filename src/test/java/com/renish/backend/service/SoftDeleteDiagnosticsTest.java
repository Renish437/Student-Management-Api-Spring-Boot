package com.renish.backend.service;

import com.renish.backend.dto.request.AddressRequest;
import com.renish.backend.dto.request.CourseRequest;
import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class SoftDeleteDiagnosticsTest {

    @Autowired
    private EntityManagerFactory emf;
    @Autowired
    private jakarta.persistence.EntityManager sharedEm;
    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AdminStudentService adminStudentService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void diagnose() {
        jdbcTemplate.execute("DELETE FROM student_courses");
        jdbcTemplate.execute("DELETE FROM students");
        jdbcTemplate.execute("DELETE FROM addresses");
        jdbcTemplate.execute("DELETE FROM courses");
        jdbcTemplate.execute("DELETE FROM departments");

        DepartmentResponse department = departmentService.createDepartment(
                DepartmentRequest.builder().departmentName("Diag Dept").build());
        CourseResponse course = courseService.createCourse(
                CourseRequest.builder().courseName("Diag Course").duration("6m").fees(10.0)
                        .instructorName("X").departmentId(department.getId()).build());
        StudentRequest request = new StudentRequest();
        request.setFirstName("Diag");
        request.setLastName("Student");
        request.setEmail("diag@sms.test");
        request.setPassword("Password1!");
        AddressRequest address = new AddressRequest();
        address.setCity("KTM");
        address.setState("Bagmati");
        address.setCountry("Nepal");
        request.setAddress(address);
        request.setDepartmentId(department.getId());
        request.setCourseIds(List.of(course.getId()));
        StudentResponse student = studentService.createStudent(request);
        Long id = student.getId();
        System.out.println("DIAG created student id=" + id);

        adminStudentService.deleteBulkStudent(List.of(id));
        System.out.println("DIAG after soft delete, db row deleted="
                + jdbcTemplate.queryForObject("SELECT deleted FROM students WHERE id = ?", Boolean.class, id));

        // 1) fresh session straight from the EMF
        EntityManager em = emf.createEntityManager();
        Session s = em.unwrap(Session.class);
        System.out.println("DIAG fresh session " + System.identityHashCode(s)
                + " get(deletedFilter)=" + s.getEnabledFilter("deletedFilter"));
        System.out.println("DIAG fresh hql sees=" + s
                .createQuery("select st from Student st", com.renish.backend.entity.Student.class)
                .getResultList().size());
        System.out.println("DIAG fresh find = " + (s.find(com.renish.backend.entity.Student.class, id) != null));
        em.close();

        // 2) transaction-bound session (what repositories use)
        new TransactionTemplate(txManager).executeWithoutResult(st -> {
            Session ts = sharedEm.unwrap(Session.class);
            System.out.println("DIAG tx-bound session " + System.identityHashCode(ts)
                    + " get(deletedFilter)=" + ts.getEnabledFilter("deletedFilter"));
            System.out.println("DIAG tx-bound find = " + (ts.find(com.renish.backend.entity.Student.class, id) != null));
            List<com.renish.backend.entity.Student> all = ts
                    .createQuery("select st from Student st", com.renish.backend.entity.Student.class)
                    .getResultList();
            System.out.println("DIAG tx-bound hql sees " + all.size() + " students, ids="
                    + all.stream().map(com.renish.backend.entity.Student::getId).toList());
            System.out.println("DIAG tx-bound repo findById = " + studentRepository.findById(id));
            System.out.println("DIAG tx-bound repo findAllById = size="
                    + studentRepository.findAllById(List.of(id)).size());

            // --- probe: does disableFilter on THIS session affect queries? ---
            ts.disableFilter("deletedFilter");
            System.out.println("PROBE after disableFilter getEnabledFilter=" + ts.getEnabledFilter("deletedFilter"));
            System.out.println("PROBE after disableFilter hql sees=" + ts
                    .createQuery("select st from Student st", com.renish.backend.entity.Student.class)
                    .getResultList().size());
            System.out.println("PROBE after disableFilter repo findAllById size="
                    + studentRepository.findAllById(List.of(id)).size());
            System.out.println("PROBE after disableFilter repo findById=" + studentRepository.findById(id));
            ts.enableFilter("deletedFilter");
            System.out.println("PROBE after re-enable hql sees=" + ts
                    .createQuery("select st from Student st", com.renish.backend.entity.Student.class)
                    .getResultList().size());
        });

        // 3) repository path (user API)
        System.out.println("DIAG repo.findById = " + studentRepository.findById(id));

        // 4) admin path through the aspect
        try {
            adminStudentService.hardDeleteStudent(id);
            System.out.println("DIAG hardDelete OK, remaining count="
                    + jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students WHERE id = ?", Long.class, id));
        } catch (Exception e) {
            System.out.println("DIAG hardDelete threw: " + e);
        }
    }
}