package com.renish.backend.service;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.CourseFilterResponse;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest req);

    PageResponse<CourseResponse> getAllCourses(Pageable pageable);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourse(UpdateCourseRequest req,Long id);
    CourseResponse patchCourse(PatchCourseRequest req, Long id);
    void deleteCourse(Long id);


    PageResponse<CourseFilterResponse> filterCourse(CourseFilterRequest req,Pageable pageable);
}
