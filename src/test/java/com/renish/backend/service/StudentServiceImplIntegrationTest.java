package com.renish.backend.service;

import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.entity.Address;
import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.repository.CourseRepository;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.repository.StudentRepository;
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
public class StudentServiceImplIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentService studentService;

    @Test
    void shouldReturnStudentById(){
        // Arrange
        Department department = new Department();
        department.setDepartmentName("Computer science");

        department = departmentRepository.save(department);

        Course course = new Course();
        course.setCourseName("Spring Boot");
        course.setDuration("6 Months");
        course.setFees(25000.0);
        course.setInstructorName("Renish");
        course.setDepartment(department);
        course = courseRepository.save(course);

        Address address = new Address();
        address.setState("Bagmati");
        address.setCountry("Nepal");
        address.setCity("Kathmandu");

        Student student = new Student();
        student.setFirstName("Pooja");
        student.setLastName("Ram");
        student.setEmail("pooja@gmail.com");
        student.setPassword("Pooja@123");
        student.setAddress(address);
        student.setDepartment(department);
        student.setCourses(List.of(course));

        Student savedStudent = studentRepository.save(student);


        // Act
        StudentResponse result = studentService.getStudentById(savedStudent.getId());


        // Assert
        assertEquals(savedStudent.getId(),result.getId());
        assertEquals("Pooja Ram",result.getName());
        assertEquals("pooja@gmail.com",result.getEmail());
        assertEquals("Kathmandu",result.getAddress().getCity());
        assertEquals("Bagmati",result.getAddress().getState());
        assertEquals("Nepal",result.getAddress().getCountry());
        assertEquals("Computer science",result.getDepartmentName());
        assertEquals(1L,result.getCourses().size());
        assertEquals("Spring Boot",result.getCourses().get(0).getCourseName());

    }


    @Test
    void shouldThrowExceptionWhenStudentNotFound(){
        assertThrows(
                ResourceNotFoundException.class,()->studentService.getStudentById(99999L)
        );
    }



}
