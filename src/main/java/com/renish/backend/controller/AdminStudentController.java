package com.renish.backend.controller;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.request.admin.AdminStudentFilterRequest;
import com.renish.backend.dto.response.*;
import com.renish.backend.dto.response.admin.AdminStudentResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.AdminStudentService;
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
@RequestMapping("/api/v1/admin/students")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.ADMIN_STUDENTS, description = SwaggerTags.ADMIN_STUDENTS_DESCRIPTION)
public class AdminStudentController {


    private final AdminStudentService adminStudentService;


    @Operation(
            summary = SwaggerOperations.AdminStudent.GET_ALL_SUMMARY,
            description = SwaggerOperations.AdminStudent.GET_ALL_DESCRIPTION
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
    public ResponseEntity<ApiResponse<PageResponse<AdminStudentResponse>>> getAllStudents(
            @ParameterObject @ModelAttribute AdminStudentFilterRequest req,
            @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC)Pageable pageable
            ){
        return ok("Admin students fetched successfully!",adminStudentService.getAllStudents(req,pageable));
    }

    @Operation(
            summary = SwaggerOperations.AdminStudent.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.AdminStudent.GET_BY_ID_DESCRIPTION
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
    public ResponseEntity<ApiResponse<AdminStudentResponse>> getStudentById(
            @Parameter(description = "ID of the student to retrieve", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Admin students fetched successfully!",adminStudentService.getStudentById(id));
    }

    @Operation(
            summary = SwaggerOperations.AdminStudent.RESTORE_SUMMARY,
            description = SwaggerOperations.AdminStudent.RESTORE_DESCRIPTION
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
                    description = "Student is not soft deleted"
            )
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<AdminStudentResponse>> restoreStudentById(
            @Parameter(description = "ID of the soft-deleted student to restore", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Admin students restored successfully!",adminStudentService.restoreStudentById(id));
    }



    @Operation(
            summary = SwaggerOperations.AdminStudent.BULK_CREATE_SUMMARY,
            description = SwaggerOperations.AdminStudent.BULK_CREATE_DESCRIPTION
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
                    responseCode = SwaggerResponses.CODE_409,
                    description = "One or more students with the same email already exist"
            )
    })
    @PostMapping("/bulk-create")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> createBulkStudent(
            @RequestBody List<@Valid StudentRequest> req) {
        return created("Students created successfully!",
                adminStudentService.createBulkStudent(req));
    }

    @Operation(
            summary = SwaggerOperations.AdminStudent.BULK_DELETE_SUMMARY,
            description = SwaggerOperations.AdminStudent.BULK_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more student IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Object>> deleteStudents(
            @Parameter(description = "IDs of the students to soft delete", required = true)
            @RequestBody List<Long> ids) {
        adminStudentService.deleteBulkStudent(ids);
        return ok("Students deleted successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.AdminStudent.HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminStudent.HARD_DELETE_DESCRIPTION
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
    public ResponseEntity<ApiResponse<Void>> hardDeleteStudent(
            @Parameter(description = "ID of the student to permanently delete", required = true, example = "1")
            @PathVariable Long id
    ){
        adminStudentService.hardDeleteStudent(id);
        return ok("Student permanently deleted successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.AdminStudent.BULK_HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminStudent.BULK_HARD_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more student IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-hard-delete")
    public ResponseEntity<ApiResponse<Void>> bulkHardDeleteStudent(
            @Parameter(description = "IDs of the students to permanently delete", required = true)
            @RequestBody List<Long> ids
    ){
        adminStudentService.bulkHardDeleteStudent(ids);
        return ok("Students permanently deleted successfully!",null);
    }



    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
