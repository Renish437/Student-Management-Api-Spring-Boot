package com.renish.backend.service;


import com.renish.backend.dto.request.PatchStudentRequest;
import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.UpdateStudentRequest;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.StudentProjectionResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.dto.response.StudentSummaryResponse;
import com.renish.backend.entity.Student;
import com.renish.backend.projection.StudentProjection;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {

    StudentResponse getStudentById(Long id);

    StudentResponse createStudent(StudentRequest req);

    StudentResponse updateStudent(Long id, UpdateStudentRequest req);

    StudentResponse patchStudent(Long id, PatchStudentRequest req);

    void deleteStudent(Long id);

    Student getByEntity(Long id);

	PageResponse<StudentResponse> getAllStudents(Pageable pageable);


    List<StudentResponse> getAllStudentSortByFirstName();

    PageResponse<StudentResponse> getStudent(int page,int size);

   PageResponse<StudentProjectionResponse> getStudentProjection(Pageable pageable);

    PageResponse<StudentResponse> searchStudents(String firstName,String email,String courseName,Double minFee,Double maxFee,String departmentName,Pageable pageable);

    PageResponse<StudentProjection> filterByTotalFees(Double minFee,Double maxFee,Pageable pageable);

    StudentResponse uploadProfileImage(Long id, MultipartFile file);

    Resource downloadProfileImage(Long id);
}
