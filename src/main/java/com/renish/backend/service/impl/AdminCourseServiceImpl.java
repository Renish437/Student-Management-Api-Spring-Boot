package com.renish.backend.service.impl;

import com.renish.backend.annotation.IncludeDeleted;
import com.renish.backend.dto.request.CourseRequest;
import com.renish.backend.dto.request.UpdateCourseStatusRequest;
import com.renish.backend.dto.request.admin.AdminCourseFilterRequest;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminCourseResponse;
import com.renish.backend.entity.Course;
import com.renish.backend.exception.DuplicateResourceException;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.mapper.AdminCourseMapper;
import com.renish.backend.mapper.CourseMapper;
import com.renish.backend.repository.CourseRepository;

import com.renish.backend.service.AdminCourseService;
import com.renish.backend.service.AdminDepartmentService;
import com.renish.backend.specification.AdminCourseSpecification;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
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

@Service
@RequiredArgsConstructor
public class AdminCourseServiceImpl implements AdminCourseService {
    private final CourseRepository courseRepository;
    private final AuditorAware<String> auditorAware;
    private final AdminDepartmentService adminDepartmentService;


    @Override
    @Transactional(readOnly = true)
    @IncludeDeleted
    @Cacheable(
            value = "adminCoursesPage",
            key = "#req.courseName + ':' + #req.duration + ':' + #req.instructorName + ':' + #req.departmentName + ':' + #req.minFee + ':' + #req.maxFee + ':' + #req.status + ':' + #req.deleted + ':' + #pageable.toString()"
    )
    public PageResponse<AdminCourseResponse> getAllCourses(AdminCourseFilterRequest req, Pageable pageable) {
        Specification<Course> courseSpecification = AdminCourseSpecification.filterCourse(req.getCourseName(),req.getDuration(),req.getInstructorName(),req.getDepartmentName(),req.getMinFee(),req.getMaxFee(),req.getStatus(),req.isDeleted());
        return PageResponse.of(courseRepository.findAll(courseSpecification,pageable).map(AdminCourseMapper::toCourseResponse));
    }
    @Override
    @Transactional
    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
    public List<CourseResponse> createBulkCourse(List<CourseRequest> req) {
        List<Course> courses = req.stream().map(this::toCreateCourse).toList();
        List<Course> savedCourses = courseRepository.saveAll(courses);
        return savedCourses.stream().map(CourseMapper::toCourseResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    @Caching(
            evict = {@CacheEvict(value = {"courses", "adminCourses"}, key = "#id"),
                    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
            }
    )
    public CourseStatusResponse updateCourseStatus(UpdateCourseStatusRequest req, Long id){
        Course course = getEntityById(id);
        course.setStatus(req.getStatus());
        return CourseMapper.toCourseStatusResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    @IncludeDeleted
    @Cacheable(value = "adminCourses", key = "#id")
    public AdminCourseResponse getCourseById(Long id) {
        Course course = getEntityById(id);
        return AdminCourseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional
    @IncludeDeleted
    @Caching(
            evict = {@CacheEvict(value = {"courses", "adminCourses"}, key = "#id"),
                    @CacheEvict(value = {"coursesPage", "adminCoursesPage"}, allEntries = true)
            }
    )
    public AdminCourseResponse restoreCourseById(Long id) {
        Course course = getEntityById(id);
        if(!course.isDeleted()){
            return AdminCourseMapper.toCourseResponse(course);
        }
        String restoredByUser = auditorAware.getCurrentAuditor().orElse("admin");
        course.restoreDeleted(restoredByUser);
        return AdminCourseMapper.toCourseResponse(course);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"courses", "adminCourses", "coursesPage", "adminCoursesPage"}, allEntries = true)
    public void deleteBulkCourse(List<Long> ids) {
        List<Course> courses = getEntitiesByIds(ids);
        String deletedByUser = auditorAware.getCurrentAuditor().orElse("admin");
        courses.forEach(c->c.markAsDeleted(deletedByUser));
    }

    @Transactional
    @IncludeDeleted
    @Override
    @CacheEvict(value = {"courses", "adminCourses", "coursesPage", "adminCoursesPage"}, allEntries = true)
    public void hardDeleteCourse(Long id) {
        bulkHardDeleteCourse(List.of(id));
    }

    @Transactional
    @IncludeDeleted
    @Override
    @CacheEvict(value = {"courses", "adminCourses", "coursesPage", "adminCoursesPage"}, allEntries = true)
    public void bulkHardDeleteCourse(List<Long> ids) {
        List<Course> courses = getEntitiesByIds(ids);
        if (courses.isEmpty()) {
            return;
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        courseRepository.deleteEnrollmentsByCourseIds(courseIds);
        courseRepository.deleteAllInBatch(courses);
    }

    private List<Course> getEntitiesByIds(List<Long> ids){
        List<Course> courses = courseRepository.findAllById(ids);
        if(courses.size()!=ids.stream().distinct().count()){
            throw new ResourceNotFoundException("One or more courses not found");
        }
        return courses;
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
                .department(adminDepartmentService.getEntityById(req.getDepartmentId()))
                .build();
    }
}
