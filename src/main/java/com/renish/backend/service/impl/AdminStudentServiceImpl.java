package com.renish.backend.service.impl;

import com.renish.backend.annotation.IncludeDeleted;
import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.admin.AdminStudentFilterRequest;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import com.renish.backend.entity.Address;
import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.AdminStudentMapper;
import com.renish.backend.mapper.StudentMapper;
import com.renish.backend.repository.CourseRepository;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.service.AdminStudentService;
import com.renish.backend.specification.AdminStudentSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStudentServiceImpl implements AdminStudentService {

    private final StudentRepository studentRepository;

    private final DepartmentRepository departmentRepository;

    private final CourseRepository courseRepository;

    private final AuditorAware<String> auditorAware;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    @IncludeDeleted
    @Cacheable(
            value = "adminStudentsPage",
            key = "#req.firstName + ':' + #req.lastName + ':' + #req.email + ':' + #req.courseName + ':' + #req.minFee + ':' + #req.maxFee + ':' + #req.departmentName + ':' + #req.status + ':' + #req.deleted + ':' + #pageable.toString()"
    )
    public PageResponse<AdminStudentResponse> getAllStudents(
            AdminStudentFilterRequest req,
            Pageable pageable
    ) {
        Specification<Student> spec =
                AdminStudentSpecification.filterStudents(
                        req.getFirstName(),
                        req.getLastName(),
                        req.getEmail(),
                        req.getCourseName(),
                        req.getMinFee(),
                        req.getMaxFee(),
                        req.getDepartmentName(),
                        req.isDeleted()
                );

        return PageResponse.of(
                studentRepository.findAll(spec, pageable)
                        .map(AdminStudentMapper::toStudentResponse)
        );
    }

    @Transactional
    @CacheEvict(value = {"adminStudentsPage", "studentsPage"}, allEntries = true)
    public List<StudentResponse> createBulkStudent(List<StudentRequest> req) {
        List<Student> students = req.stream()
                .map(this::toStudent)
                .toList();

        return studentRepository.saveAll(students).stream()
                .map(StudentMapper::toStudentResponse)
                .toList();
    }

    @Transactional
    @IncludeDeleted
    @CacheEvict(value = {"students", "adminStudents", "studentsPage", "adminStudentsPage"}, allEntries = true)
    @Override
    public void deleteBulkStudent(List<Long> ids) {
        List<Student> students = getByEntitiesByIds(ids);

        String user = auditorAware.getCurrentAuditor()
                .orElse("admin");
        students.forEach(student -> student.markAsDeleted(user));
    }

    @Override
    @IncludeDeleted
    @Transactional(readOnly = true)
    @Cacheable(value = "adminStudents", key = "#id")
    public AdminStudentResponse getStudentById(Long id) {
        Student student = getByEntity(id);
        return AdminStudentMapper.toStudentResponse(student);
    }

    @Override
    @Transactional
    @IncludeDeleted
    @Caching(evict = {
            @CacheEvict(value = "students", key = "#id"),
            @CacheEvict(value = "adminStudents", key = "#id"),
            @CacheEvict(value = {"studentsPage", "adminStudentsPage"}, allEntries = true)
    })
    public AdminStudentResponse restoreStudentById(Long id) {
        Student student = getByEntity(id);
        if(!student.isDeleted()){
            return AdminStudentMapper.toStudentResponse(student);
        }
        String restoredByUser = auditorAware.getCurrentAuditor().orElse("admin");
        student.restoreDeleted(restoredByUser);
        return AdminStudentMapper.toStudentResponse(student);
    }

    @Transactional
    @IncludeDeleted
    @Override
    @CacheEvict(value = {"students", "adminStudents", "studentsPage", "adminStudentsPage"}, allEntries = true)
    public void hardDeleteStudent(Long id) {
        bulkHardDeleteStudent(List.of(id));
    }

    @Transactional
    @IncludeDeleted
    @Override
    @CacheEvict(value = {"students", "adminStudents", "studentsPage", "adminStudentsPage"}, allEntries = true)
    public void bulkHardDeleteStudent(List<Long> ids) {
        List<Student> students = getByEntitiesByIds(ids);
        if (students.isEmpty()) {
            return;
        }
        List<Long> studentIds = students.stream().map(Student::getId).toList();

        // enrollment rows first, otherwise the student_courses foreign key blocks the delete
        studentRepository.deleteEnrollmentsByStudentIds(studentIds);
        List<Long> addressIds = studentRepository.findAddressIdsByStudentIds(studentIds);
        studentRepository.deleteAllInBatch(students);
        // addresses are referenced by students.address_id, so they can only go once the students are gone
        if (!addressIds.isEmpty()) {
            studentRepository.deleteAddressesByIds(addressIds);
        }
    }

    private List<Student> getByEntitiesByIds(List<Long> ids) {
        List<Student> students = studentRepository.findAllById(ids);
        if (students.size() != ids.stream().distinct().count()) {
            throw new ResourceNotFoundException("One or more students not found");
        }
        return students;
    }

    public Student getByEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                            log.warn("Student not found with id: {} ", id);
                            return new ResourceNotFoundException("Student not found with id: " + id);
                        }
                );

    }
    private Student toStudent(StudentRequest req) {

        if (studentRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException(
                    "Student already exists with email: " + req.getEmail());
        }

        Department department = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + req.getDepartmentId()));

        Address address = Address.builder()
                .city(req.getAddress().getCity())
                .state(req.getAddress().getState())
                .country(req.getAddress().getCountry())
                .build();

        List<Course> courses = req.getCourseIds() == null
                ? new java.util.ArrayList<>()
                : courseRepository.findAllById(req.getCourseIds());

        if (req.getCourseIds() != null
                && courses.size() != req.getCourseIds().stream().distinct().count()) {
            throw new ResourceNotFoundException("One or more courses not found");
        }

        return Student.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .address(address)
                .department(department)
                .courses(courses)
                .build();
    }

}