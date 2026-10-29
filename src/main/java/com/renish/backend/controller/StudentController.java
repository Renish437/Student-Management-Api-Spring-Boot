package com.renish.backend.controller;


import com.renish.backend.dto.request.PatchStudentRequest;
import com.renish.backend.dto.request.StudentRequest;
import com.renish.backend.dto.request.UpdateStudentRequest;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.StudentProjectionResponse;
import com.renish.backend.dto.response.StudentResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.projection.StudentProjection;
import com.renish.backend.service.StudentService;
import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.STUDENTS, description = SwaggerTags.STUDENTS_DESCRIPTION)
public class StudentController {

    private final StudentService studentService;

    @Operation(
            summary = SwaggerOperations.Student.GET_ALL_SUMMARY,
            description = SwaggerOperations.Student.GET_ALL_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_500,
                    description = SwaggerResponses.SERVER_ERROR
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable) {
        PageResponse<StudentResponse> res = studentService.getAllStudents(pageable);
        return ResponseEntity
                .ok(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Students fetched successfully",
                        res));
    }


    @GetMapping("/page")
    @Operation(
            summary = SwaggerOperations.Student.GET_PAGED_SUMMARY,
            description = SwaggerOperations.Student.GET_PAGED_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudentsByPage(
            @Parameter(description = "Page number (zero based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity
                .ok(ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Students fetched successfully",
                        studentService.getStudent(page, size)));
    }

    @Operation(
            summary = SwaggerOperations.Student.GET_PROJECTION_SUMMARY,
            description = SwaggerOperations.Student.GET_PROJECTION_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            )
    })
    @GetMapping("/projection/dto")
    public ResponseEntity<ApiResponse<PageResponse<StudentProjectionResponse>>> getStudentProjection(@ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable){
     return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Students fetched successfully!", studentService.getStudentProjection(pageable)));
    }
    // @GetMapping("/projection")
    // public ResponseEntity<ApiResponse<Page<StudentProjection>>> getStudentProjection(@ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable){
    //  return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Students fetched successfully!", studentService.getStudentProjection(pageable)));
    // }


    @Operation(
            summary = SwaggerOperations.Student.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.Student.GET_BY_ID_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @Parameter(description = "ID of the student to retrieve", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Student fetched successfully!",
                studentService.getStudentById(id)));
    }

    @Operation(
            summary = SwaggerOperations.Student.CREATE_SUMMARY,
            description = SwaggerOperations.Student.CREATE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_201,
                    description = SwaggerResponses.CREATED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "Department or course referenced by the request does not exist"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_409,
                    description = "A student with the same email already exists"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> saveStudent(@Valid @RequestBody StudentRequest student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Student created successfully!",
                studentService.createStudent(student)));
    }


    @Operation(
            summary = SwaggerOperations.Student.UPDATE_SUMMARY,
            description = SwaggerOperations.Student.UPDATE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @Parameter(description = "ID of the student to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest student) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Student updated successfully!",
                studentService.updateStudent(id, student)));
    }


    @Operation(
            summary = SwaggerOperations.Student.PATCH_SUMMARY,
            description = SwaggerOperations.Student.PATCH_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> patchStudent(
            @Parameter(description = "ID of the student to partially update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PatchStudentRequest student) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Student updated successfully!",
                studentService.patchStudent(id, student)));
    }

    @Operation(
            summary = SwaggerOperations.Student.DELETE_SUMMARY,
            description = SwaggerOperations.Student.DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteStudent(
            @Parameter(description = "ID of the student to delete", required = true, example = "1")
            @PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Student deleted successfully!",
                null));
    }



    @Operation(
            summary = SwaggerOperations.Student.SORT_SUMMARY,
            description = SwaggerOperations.Student.SORT_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            )
    })
    @GetMapping("/sort")
    public ResponseEntity<List<StudentResponse>> getSortedStudents() {
        return ResponseEntity.ok(studentService.getAllStudentSortByFirstName());
    }



    @Operation(
            summary = SwaggerOperations.Student.FILTER_SUMMARY,
            description = SwaggerOperations.Student.FILTER_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            )
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> searchStudents(
        @Parameter(description = "Filter by student first name")
        @RequestParam(required = false) String firstName,
        @Parameter(description = "Filter by student email")
        @RequestParam(required = false) String email,
        @Parameter(description = "Filter by enrolled course name")
        @RequestParam(required = false) String courseName,
        @Parameter(description = "Filter by department name")
        @RequestParam(required = false) String departmentName,
        @Parameter(description = "Minimum course fee")
        @RequestParam(required = false) Double minFee,
        @Parameter(description = "Maximum course fee")
        @RequestParam(required = false) Double maxFee,
        @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ok("Student filtered successfully!", studentService.searchStudents(firstName, email, courseName,minFee,maxFee,departmentName, pageable));
    }

    @Operation(
            summary = SwaggerOperations.Student.FILTER_BY_TOTAL_FEES_SUMMARY,
            description = SwaggerOperations.Student.FILTER_BY_TOTAL_FEES_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_403,
                    description = SwaggerResponses.FORBIDDEN
            )
    })
    @GetMapping("/filter-by-total-fees")
    public ResponseEntity<ApiResponse<PageResponse<StudentProjection>>> filterByTotalFees(
            @Parameter(description = "Minimum total fee", required = true, example = "1000")
            @RequestParam Double minFee,
            @Parameter(description = "Maximum total fee", required = true, example = "50000")
            @RequestParam Double maxFee,
            @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ok("Student filtered by total fees", studentService.filterByTotalFees(minFee, maxFee, pageable));
    }

    @Operation(
            summary = SwaggerOperations.Student.UPLOAD_IMAGE_SUMMARY,
            description = SwaggerOperations.Student.UPLOAD_IMAGE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.IMAGE_UPLOADED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.IMAGE_INVALID_FILE
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.IMAGE_STUDENT_NOT_FOUND
            )
    })
    @PostMapping(value = "/{id}/image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StudentResponse>> uploadProfileImage(
            @Parameter(description = "ID of the student whose image is uploaded", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Image file (JPG, PNG or WEBP), max size limited by the server", required = true)
            @RequestParam("file") MultipartFile file
            ){
        return ok("Image uploaded successfully!",studentService.uploadProfileImage(id,file));
    }

    @Operation(
            summary = SwaggerOperations.Student.DOWNLOAD_IMAGE_SUMMARY,
            description = SwaggerOperations.Student.DOWNLOAD_IMAGE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.IMAGE_DOWNLOADED,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.IMAGE_NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_500,
                    description = SwaggerResponses.IMAGE_DOWNLOAD_ERROR
            )
    })
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> downloadProfileImage(
            @Parameter(description = "ID of the student whose image is downloaded", required = true, example = "1")
            @PathVariable Long id) {

        Resource resource = studentService.downloadProfileImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }
    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }
    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }



//    @GetMapping("/header")
//   public ResponseEntity<String> getClientVersion(@RequestHeader("X-Client-Version") String version){
//        return ResponseEntity.ok("Client version: "+version);
//   }
//    @GetMapping("/custom-find-student-by-department")
//  public ResponseEntity<ApiResponse<List<StudentResponse>>> getCustomStudentsByDepartment(
//            @RequestParam String departmentName
//    ){
//        return  ok("Custom students fetched successfully!",studentService.getCustomStudentsByDepartment(departmentName));
//    }


    //    @GetMapping("/custom-students")
//    public ResponseEntity<ApiResponse<List<StudentResponse>>> getCustomStudents(){
//        return  ok("Custom students fetched successfully!",studentService.getAllCustomStudents());
//    }

}
