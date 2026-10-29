package com.renish.backend.controller;

import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.UpdateDepartmentStatusRequest;
import com.renish.backend.dto.request.admin.AdminDepartmentFilterRequest;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.AdminDepartmentService;
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
@RequestMapping("/api/v1/admin/departments")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.ADMIN_DEPARTMENTS, description = SwaggerTags.ADMIN_DEPARTMENTS_DESCRIPTION)
public class AdminDepartmentController {

    private final AdminDepartmentService adminDepartmentService;


    @Operation(
            summary = SwaggerOperations.AdminDepartment.GET_ALL_SUMMARY,
            description = SwaggerOperations.AdminDepartment.GET_ALL_DESCRIPTION
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
    public ResponseEntity<ApiResponse<PageResponse<AdminDepartmentResponse>>> getAllDepartments(
            @ParameterObject @ModelAttribute AdminDepartmentFilterRequest req,
            @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ok("Admin departments fetched successfully!",adminDepartmentService.getAllDepartments(req,pageable));
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.AdminDepartment.GET_BY_ID_DESCRIPTION
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
    public ResponseEntity<ApiResponse<AdminDepartmentResponse>> getDepartmentById(
            @Parameter(description = "ID of the department to retrieve", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Department fetched successfully!",adminDepartmentService.getDepartmentById(id));
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.RESTORE_SUMMARY,
            description = SwaggerOperations.AdminDepartment.RESTORE_DESCRIPTION
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
                    description = "Department is not soft deleted"
            )
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<AdminDepartmentResponse>> restoreDepartmentById(
            @Parameter(description = "ID of the soft-deleted department to restore", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Department restored successfully!",adminDepartmentService.restoreDepartmentById(id));
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.BULK_CREATE_SUMMARY,
            description = SwaggerOperations.AdminDepartment.BULK_CREATE_DESCRIPTION
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
                    description = "One or more departments with the same name already exist"
            )
    })
    @PostMapping("/bulk-create")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> createDepartmentList(
            @RequestBody List<@Valid DepartmentRequest> req) {
        return created("Departments created successfully!", adminDepartmentService.createBulkDepartment(req));
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.UPDATE_STATUS_SUMMARY,
            description = SwaggerOperations.AdminDepartment.UPDATE_STATUS_DESCRIPTION
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
    public ResponseEntity<ApiResponse<DepartmentStatusResponse>> updateDepartmentStatus(
            @Parameter(description = "ID of the department whose status changes", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentStatusRequest req) {
        return ok("Department status updated successfully!", adminDepartmentService.updateDepartmentStatus(req, id));
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.BULK_DELETE_SUMMARY,
            description = SwaggerOperations.AdminDepartment.BULK_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more department IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Void>> deleteDepartments(
            @Parameter(description = "IDs of the departments to soft delete", required = true)
            @RequestBody List<Long> ids) {

        adminDepartmentService.deleteBulkDepartment(ids);
        return ok("Departments deleted successfully!", null);
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminDepartment.HARD_DELETE_DESCRIPTION
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
    public ResponseEntity<ApiResponse<Void>> hardDeleteDepartment(
            @Parameter(description = "ID of the department to permanently delete", required = true, example = "1")
            @PathVariable Long id
    ){
        adminDepartmentService.hardDeleteDepartment(id);
        return ok("Department permanently deleted successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.AdminDepartment.BULK_HARD_DELETE_SUMMARY,
            description = SwaggerOperations.AdminDepartment.BULK_HARD_DELETE_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = "One or more department IDs do not exist"
            )
    })
    @DeleteMapping("/bulk-hard-delete")
    public ResponseEntity<ApiResponse<Void>> bulkHardDeleteDepartment(
            @Parameter(description = "IDs of the departments to permanently delete", required = true)
            @RequestBody List<Long> ids
    ){
        adminDepartmentService.bulkHardDeleteDepartment(ids);
        return ok("Departments permanently deleted successfully!",null);
    }



    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
