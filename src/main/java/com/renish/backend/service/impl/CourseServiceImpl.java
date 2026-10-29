package com.renish.backend.service.impl;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.CourseFilterResponse;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.CourseMapper;
import com.renish.backend.repository.CourseRepository;
import com.renish.backend.repository.DepartmentRepository;
import com.renish.backend.service.CourseService;
import com.renish.backend.service.DepartmentService;
import com.renish.backend.specification.CourseSpecification;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;
    private final AuditorAware<String> auditorAware;

    @Override
    @Transactional
    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
    public CourseResponse createCourse(CourseRequest req) {
      Course savedCourse =  courseRepository.save(toCreateCourse(req));
      return CourseMapper.toCourseResponse(savedCourse);
    }




    @Override
    @Cacheable(value = "coursesPage",key = "#pageable.toString()")
    public PageResponse<CourseResponse> getAllCourses(Pageable pageable) {
        Page<CourseResponse> page = courseRepository.findAll(pageable).map(CourseMapper::toCourseResponse);
     return PageResponse.of(page);
    }

    @Override
    @Cacheable(value = "courses",key = "#id")
    public CourseResponse getCourseById(Long id) {
        Course course = getEntityById(id);
        return CourseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "courses",key = "#id"),
            },
            evict = {
                    @CacheEvict(value = "adminCourses", key = "#id"),
                    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
    })
    public CourseResponse updateCourse(UpdateCourseRequest req, Long id) {
       Course course = getEntityById(id);        if (!Objects.equals(course.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Course has been modified by another user. " +
                            "Current version is: " + course.getVersion() +
                            ". Please refresh and try again."
            );
        }
       Department department = departmentService.getEntityById(req.getDepartmentId());
       if(courseRepository.existsByCourseNameAndDepartmentIdAndIdNot(req.getCourseName(),department.getId(),id)){
           throw  new DuplicateResourceException( "Course name already exists in this department: "
                   + req.getCourseName());
       }
       course.setCourseName(req.getCourseName());
       course.setDuration(req.getDuration());
       course.setFees(req.getFees());
       course.setInstructorName(req.getInstructorName());
       course.setDepartment(department);

       return CourseMapper.toCourseResponse(course);
    }


    @Override
    @Transactional
    @Caching(
            put = { @CachePut(value = "courses",key = "#id") },
            evict = {
                    @CacheEvict(value = "adminCourses", key = "#id"),
                    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
            }
    )
    public CourseResponse patchCourse(PatchCourseRequest req, Long id) {
        Course course = getEntityById(id);
        if (!Objects.equals(course.getVersion(), req.getVersion())) {
            throw new OptimisticLockException(
                    "Course has been modified by another user. " +
                            "Current version is: " + course.getVersion() +
                            ". Please refresh and try again."
            );
        }
        String courseName = req.getCourseName() != null
                ? req.getCourseName()
                : course.getCourseName();

        Department department = req.getDepartmentId() != null
                ? departmentService.getEntityById(req.getDepartmentId())
                : course.getDepartment();

        Long departmentId = department.getId();

        if (courseRepository.existsByCourseNameAndDepartmentIdAndIdNot(
                courseName, departmentId, id)) {
            throw new DuplicateResourceException(
                    "Course name already exists in this department: " + courseName
            );
        }

        if (req.getCourseName() != null) {
            course.setCourseName(req.getCourseName());
        }

        if (req.getDuration() != null) {
            course.setDuration(req.getDuration());
        }

        if (req.getFees() != null) {
            course.setFees(req.getFees());
        }

        if (req.getInstructorName() != null) {
            course.setInstructorName(req.getInstructorName());
        }

        if (req.getDepartmentId() != null) {
            course.setDepartment(department);
        }

        return CourseMapper.toCourseResponse(course);
    }


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "courses",key = "#id"),
                    @CacheEvict(value = "adminCourses", key = "#id"),
                    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
            }
    )
    public void deleteCourse(Long id) {
    Course existingCourse = getEntityById(id);
    String deletedByUser = auditorAware.getCurrentAuditor().orElse("admin");
    existingCourse.markAsDeleted(deletedByUser);
    }

    private Course getEntityById(Long id){
        return courseRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Course not found with id: "+id));
    }


    private Course toCreateCourse(CourseRequest req){
        if(courseRepository.existsByCourseNameAndDepartmentId(req.getCourseName(),req.getDepartmentId())){
            throw new DuplicateResourceException("Course name already exists in this department: "+req.getCourseName());
        }


        return Course.builder()
                .courseName(req.getCourseName())
                .duration(req.getDuration())
                .fees(req.getFees())
                .instructorName(req.getInstructorName())
                .department(departmentService.getEntityById(req.getDepartmentId()))
                .build();
    }
    

    @Override
    public PageResponse<CourseFilterResponse> filterCourse(CourseFilterRequest req,Pageable pageable) {
        Specification<Course> spec = CourseSpecification.filterCourse(
            req.getCourseName(), req.getDuration(),req.getInstructorName(),
             req.getDepartmentName(),req.getMinFee(),req.getMaxFee(),
             req.getStatus()
            );


        Page<Course> courses= courseRepository.findAllWithDepartment(spec, pageable);

        return PageResponse.of(courses.map(CourseMapper::toCourseFilterResponse));
        
    }

}

