package com.renish.backend.service;


import com.renish.backend.dto.request.CourseRequest;
import com.renish.backend.dto.request.UpdateCourseStatusRequest;
import com.renish.backend.dto.request.admin.AdminCourseFilterRequest;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminCourseResponse;
import com.renish.backend.repository.CourseRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminCourseService {

  PageResponse<AdminCourseResponse>  getAllCourses(AdminCourseFilterRequest req, Pageable pageable);
    CourseStatusResponse updateCourseStatus(UpdateCourseStatusRequest req, Long id);
    AdminCourseResponse getCourseById(Long id);
    AdminCourseResponse restoreCourseById(Long id);
    List<CourseResponse> createBulkCourse(List<CourseRequest> req);
    void deleteBulkCourse(List<Long> ids);
    void hardDeleteCourse(Long id);
    void bulkHardDeleteCourse(List<Long> ids);


}
