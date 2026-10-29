package com.renish.backend.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.renish.backend.config.AuditorAwareImpl;

import com.renish.backend.dto.request.PatchStudentRequest;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.entity.Course;
import com.renish.backend.exception.FileUploadException;
import com.renish.backend.repository.CourseRepository;

import com.renish.backend.service.FileStorageService;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.UpdateStudentRequest;
import com.renish.backend.dto.response.StudentProjectionResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.entity.Address;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.StudentMapper;
import com.renish.backend.projection.StudentProjection;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.repository.StudentRepository;
import com.renish.backend.service.StudentService;
import com.renish.backend.specification.StudentSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final DepartmentRepository departmentRepository;

    private final CourseRepository courseRepository;

    private final AuditorAware<String> auditorAware;

    private final PasswordEncoder passwordEncoder;

    private final FileStorageService fileStorageService;


    // In StudentServiceImpl

    @Override
    @Cacheable(
            value = "studentsPage",
            key = "#pageable.toString()"
    )
    public PageResponse<StudentResponse> getAllStudents(Pageable pageable) {
        Page<StudentResponse> page = studentRepository.findAll(pageable).map(StudentMapper::toStudentResponse);
        return PageResponse.of(page);
    }

    @Override
    @Cacheable(value = "students",key = "#id")
    public StudentResponse getStudentById(Long id) {
        log.debug("Fetching student with id: {}",id);
        Student existingStudent = getByEntity(id);

        return StudentMapper.toStudentResponse(existingStudent);
    }

    @Transactional
    @CacheEvict(value = {"studentsPage", "adminStudentsPage"}, allEntries = true)
    public StudentResponse createStudent(StudentRequest req) {
        return StudentMapper.toStudentResponse(
                studentRepository.save(toStudent(req)));
    }


    @Transactional
//    @CachePut(value = "students",key = "#id")
    @Caching(evict = {
            @CacheEvict(value = "students",key = "#id"),
            @CacheEvict(value = "adminStudents",key = "#id"),
            @CacheEvict(value = {"studentsPage", "adminStudentsPage"},allEntries = true)
    })
    public StudentResponse updateStudent(Long id, UpdateStudentRequest req) {
        Student existingStudent = getByEntity(id);
        if (!Objects.equals(existingStudent.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Student has been modified by another user. " +
                            "Current version is: " + existingStudent.getVersion() +
                            ". Please refresh and try again."
            );
        }
        // Duplicate email check
        // if (studentRepository.existsByEmail(req.getEmail()) &&
        // !existingStudent.getEmail().equals(req.getEmail())) {
        // throw new DuplicateResourceException("Student already exists with email: " +
        // req.getEmail());
        // }
        if (req.getEmail() != null
                && studentRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new DuplicateResourceException(
                    "Student already exists with email: " + req.getEmail());
        }
        existingStudent.setFirstName(req.getFirstName());
        existingStudent.setLastName(req.getLastName());
        existingStudent.setEmail(req.getEmail());
        existingStudent.setPassword(passwordEncoder.encode(req.getPassword()));
        if(req.getAddress()!=null){

        Address address = existingStudent.getAddress();
        if (address == null) {
            address = new Address();
            existingStudent.setAddress(address);
        }
        address.setCity(req.getAddress().getCity());
        address.setState(req.getAddress().getState());
        address.setCountry(req.getAddress().getCountry());
        }

        Department department = departmentRepository.findById(req.getDepartmentId()).orElseThrow(
                () -> new ResourceNotFoundException("Department not found with id: " + req.getDepartmentId()));
        existingStudent.setDepartment(department);

        List<Course> courses = courseRepository.findAllById(req.getCourseIds());

        if (courses.size() != req.getCourseIds().stream().distinct().count()) {
            throw new ResourceNotFoundException("One or more courses not found");
        }

        existingStudent.setCourses(courses);

//        Student savedStudent =studentRepository.save(existingStudent);

        return StudentMapper.toStudentResponse(existingStudent);

    }

    @Override
//    @CachePut(value = "students",key = "#id")
    @Caching(evict = {
            @CacheEvict(value = "students",key = "#id"),
            @CacheEvict(value = "adminStudents",key = "#id"),
            @CacheEvict(value = {"studentsPage", "adminStudentsPage"},allEntries = true)
    })
// For checked exceptions like IOException SQLException etc.   @Transactional(rollbackFor = Exception.class)
    @Transactional
    public StudentResponse patchStudent(Long id, PatchStudentRequest req) {
        Student existingStudent = getByEntity(id);
        if (!Objects.equals(existingStudent.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Student has been modified by another user. " +
                            "Current version is: " + existingStudent.getVersion() +
                            ". Please refresh and try again."
            );
        }
        if (req.getFirstName() != null) {
            existingStudent.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null) {
            existingStudent.setLastName(req.getLastName());
        }

        // Duplicate email check
        // if (studentRepository.existsByEmail(req.getEmail()) &&
        // !existingStudent.getEmail().equals(req.getEmail())) {
        // throw new DuplicateResourceException("Student already exists with email: " +
        // req.getEmail());
        // }
        if (req.getEmail() != null
                && studentRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new DuplicateResourceException(
                    "Student already exists with email: " + req.getEmail());
        }
        if(req.getEmail()!=null){
            existingStudent.setEmail(req.getEmail());
        }
        if (req.getPassword() != null) {
            existingStudent.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getAddress() != null) {
            Address address = existingStudent.getAddress();
            if (address == null) {
                address = new Address();
                existingStudent.setAddress(address);
            }
            if (req.getAddress().getCity() != null) {
                address.setCity(req.getAddress().getCity());
            }
            if (req.getAddress().getState() != null) {
                address.setState(req.getAddress().getState());
            }
            if (req.getAddress().getCountry() != null) {
                address.setCountry(req.getAddress().getCountry());
            }

        }
        if (req.getDepartmentId() != null) {
            Department department = departmentRepository.findById(req.getDepartmentId()).orElseThrow(
                    () -> new ResourceNotFoundException("Department not found with id: " + req.getDepartmentId()));
            existingStudent.setDepartment(department);
        }
        if (req.getCourseIds() != null) {
            List<Course> courses = courseRepository.findAllById(req.getCourseIds());

            if (courses.size() != req.getCourseIds().stream().distinct().count()) {
                throw new ResourceNotFoundException("One or more courses not found");
            }
            existingStudent.setCourses(courses);
        }
//        Student savedStudent =studentRepository.save(existingStudent);

        return StudentMapper.toStudentResponse(existingStudent);
    }

    @Transactional
//    @@CacheEvict(value = "students",allEntries = true)
//    @CacheEvict(value = "students",key = "#id")
    @Caching(evict = {
            @CacheEvict(value = "students",key = "#id"),
            @CacheEvict(value = "adminStudents",key = "#id"),
            @CacheEvict(value = {"studentsPage", "adminStudentsPage"},allEntries = true)
    })
    public void deleteStudent(Long id) {
        Student existingStudent = getByEntity(id);

        String user = auditorAware.getCurrentAuditor()
                .orElse("admin");

        existingStudent.markAsDeleted(user);
    }




    public Long getStudentCountByCourse(String courseName){
        return studentRepository.countStudentByCourse(courseName);
    }

    public List<StudentResponse> searchStudent(String firstName, String course) {
        return studentRepository.findByFirstNameAndCourse(firstName, course).stream()
                .map(StudentMapper::toStudentResponse).toList();
    }

    public List<StudentResponse> searchKeyword(String keyword) {
        return studentRepository.findByFirstNameContaining(keyword).stream().map(StudentMapper::toStudentResponse)
                .toList();
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

    @Override
    public List<StudentResponse> getAllStudentSortByFirstName() {
        Sort sort = Sort.by("firstName").ascending().and(Sort.by("lastName").descending());
        List<Student> students = studentRepository.findAll(
                sort);
        return students.stream().map(StudentMapper::toStudentResponse).toList();
    }

    @Override
    @Cacheable(value = "studentsPage", key = "#page + ':' + #size")
    public PageResponse<StudentResponse> getStudent(int page, int size) {
        Sort sort = Sort.by("firstName").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> student = studentRepository.findAll(pageable);
        return PageResponse.of(student.map(StudentMapper::toStudentResponse));
    }

    @Override
    public PageResponse<StudentProjectionResponse> getStudentProjection(Pageable pageable) {
        return PageResponse.of(studentRepository.getStudentProjection(pageable));
        // return studentRepository.findBy(pageable);
    }

    @Override
    public PageResponse<StudentResponse> searchStudents(String firstName, String email, String courseName,Double minFee,Double maxFee,String departmentName, Pageable pageable) {
        Specification<Student> specification = Specification.allOf(
                StudentSpecification.hasFirstName(firstName), StudentSpecification.hasEmail(email),
                StudentSpecification.hasCourseNameAndFee(courseName,minFee,maxFee),
                StudentSpecification.hasDepartmentName(departmentName)
            );

        Page<Student> students = studentRepository.findAllWithRelations(specification, pageable);
        return PageResponse.of(students.map(StudentMapper::toStudentResponse));
    }

    @Override
    public PageResponse<StudentProjection> filterByTotalFees(Double minFee, Double maxFee, Pageable pageable) {
        Page<StudentProjection> students = studentRepository.findByStudentsTotalFees(minFee, maxFee, pageable);
        return PageResponse.of(students);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "students",key = "#id"),
            @CacheEvict(value = "adminStudents",key = "#id"),
            @CacheEvict(value = {"studentsPage", "adminStudentsPage"},allEntries = true)
    })
    public StudentResponse uploadProfileImage(Long id, MultipartFile file) {
        Student student = getByEntity(id);

        if(file.isEmpty()){
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();

        if (!List.of("image/jpeg", "image/png", "image/webp")
                        .contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG, and WEBP images are allowed"
            );
        }
        if(file.getSize()>5*1024*1024){
            throw new IllegalArgumentException("File size cannot exceed more than 5MB");
        }

        String previousFileName = student.getProfileImage();
        String fileName;
        try {
            fileName = fileStorageService.storeFile(file);
        } catch (IOException e) {
            throw new FileUploadException("Failed to store profile image ",e);
        }

        Student updatedStudent;
        try {
            student.setProfileImage(fileName);
            updatedStudent = studentRepository.save(student);
        } catch (RuntimeException e) {
            // DB update failed: drop the file we just stored so it does not become orphaned.
            fileStorageService.deleteFile(fileName);
            throw e;
        }

        deleteFileAfterCommit(previousFileName, fileName);
        return StudentMapper.toStudentResponse(updatedStudent);
    }

    /**
     * Removes the previously stored profile image only after the transaction that
     * replaced it has been committed, so a rollback never loses the old file.
     */
    private void deleteFileAfterCommit(String previousFileName, String currentFileName) {
        if (previousFileName == null || previousFileName.isBlank()
                || previousFileName.equals(currentFileName)) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            fileStorageService.deleteFile(previousFileName);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileStorageService.deleteFile(previousFileName);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    // rolled back: keep the old file, drop the unused new one
                    fileStorageService.deleteFile(currentFileName);
                }
            }
        });
    }

    @Override
    public Resource downloadProfileImage(Long id) {
        Student student = getByEntity(id);
        String fileName = student.getProfileImage();

        if(fileName==null || fileName.isBlank()){
            throw new ResourceNotFoundException("Profile image not found for student with id: "+id);
        }
        try {

            return fileStorageService.loadFileAsResource(fileName);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Profile image file not found: "+fileName);
        }
    }

//    @Override
//    public List<StudentResponse> getAllCustomStudents() {
//        return studentRepository.findStudentCustom().stream().map(StudentMapper::toStudentResponse).toList();
//
//    }
//
//    @Override
//    public List<StudentResponse> getCustomStudentsByDepartment(String department) {
//        return studentRepository.findStudentByDepartment(department).stream().map(StudentMapper::toStudentResponse).toList();
//    }


}
