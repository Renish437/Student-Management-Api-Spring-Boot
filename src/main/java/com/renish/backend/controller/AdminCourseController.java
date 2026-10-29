package com.renish.backend.controller;


import com.renish.backend.dto.request.CourseRequest;
import com.renish.backend.dto.request.UpdateCourseStatusRequest;
import com.renish.backend.dto.request.admin.AdminCourseFilterRequest;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminCourseResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.AdminCourseService;
import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/courses")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.ADMIN_COURSES, description = SwaggerTags.ADMIN_COURSES_DESCRIPTION)
public class AdminCourseController {
    private final AdminCourseService adminCourseService;

    @Operation(
            summary = SwaggerOperations.AdminCourse.GET_ALL_SUMMARY,
            description = SwaggerOperations.AdminCourse.GET_ALL_DESCRIPTION
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
                    responseCode = SwaggerResponses.CODE_500,
                    description = SwaggerResponses.SERVER_ERROR
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminCourseResponse>>> getAllCourses(
            @ParameterObject @ModelAttribute AdminCourseFilterRequest req,
            @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ok("Admin courses fetched successfully!",adminCourseService.getAllCourses(req,pageable));
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.BULK_CREATE_SUMMARY,
            description = SwaggerOperations.AdminCourse.BULK_CREATE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_201,
                    description = SwaggerResponses.CREATED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.BAD_REQUEST
            )
    })
    @PostMapping("/bulk-create")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> createCourse(
            @RequestBody List<@Valid CourseRequest> req){
        return created("Courses created successfully!",adminCourseService.createBulkCourse(req));
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.UPDATE_STATUS_SUMMARY,
            description = SwaggerOperations.AdminCourse.UPDATE_STATUS_DESCRIPTION
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
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @PatchMapping("/status/{id}")
    public ResponseEntity<ApiResponse<CourseStatusResponse>> updateCourseStatus(
            @Parameter(description = "ID of the course whose status changes", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseStatusRequest req
    ){
        return ok("Course status updated successfully!",adminCourseService.updateCourseStatus(req,id));
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.AdminCourse.GET_BY_ID_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCourseResponse>> getCourseById(
            @Parameter(description = "ID of the course to retrieve", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Course fetched successfully!",adminCourseService.getCourseById(id));
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.RESTORE_SUMMARY,
            description = SwaggerOperations.AdminCourse.RESTORE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.OK
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_409,
                    description = "Course is not soft deleted"
            )
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<AdminCourseResponse>> restoreCourseById(
            @Parameter(description = "ID of the soft-deleted course to restore", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Course restored successfully!",adminCourseService.restoreCourseById(id));
    }


    @Operation(
            summary = SwaggerOperations.AdminCourse.BULK_DELETE_SUMMARY,
            description = SwaggerOperations.AdminCourse.BULK_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more course IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Void>> deleteBulkCourses(
            @Parameter(description = "IDs of the courses to soft delete", required = true)
            @RequestBody List<Long> ids
    ){
        adminCourseService.deleteBulkCourse(ids);
        return  ok("Courses deleted successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminCourse.HARD_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
    @DeleteMapping("/{id}/hard-delete")
    public ResponseEntity<ApiResponse<Void>> hardDeleteCourse(
            @Parameter(description = "ID of the course to permanently delete", required = true, example = "1")
            @PathVariable Long id
    ){
        adminCourseService.hardDeleteCourse(id);
        return ok("Course permanently deleted successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.AdminCourse.BULK_HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminCourse.BULK_HARD_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more course IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-hard-delete")
    public ResponseEntity<ApiResponse<Void>> bulkHardDeleteCourse(
            @Parameter(description = "IDs of the courses to permanently delete", required = true)
            @RequestBody List<Long> ids
    ){
        adminCourseService.bulkHardDeleteCourse(ids);
        return ok("Courses permanently deleted successfully!",null);
    }

    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
