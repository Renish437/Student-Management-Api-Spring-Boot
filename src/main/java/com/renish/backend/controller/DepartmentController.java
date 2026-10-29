package com.renish.backend.controller;

import java.util.List;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.DepartmentFilterResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.DepartmentStatusResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.DepartmentService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.DEPARTMENTS, description = SwaggerTags.DEPARTMENTS_DESCRIPTION)
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(
            summary = SwaggerOperations.Department.CREATE_SUMMARY,
            description = SwaggerOperations.Department.CREATE_DESCRIPTION
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
                    description = "A department with the same name already exists"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest req) {
        return created("Department created successfully!", departmentService.createDepartment(req));
    }


    @Operation(
            summary = SwaggerOperations.Department.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.Department.GET_BY_ID_DESCRIPTION
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
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.NOT_FOUND
            )
    })
   @GetMapping("/{id}")
   public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
           @Parameter(description = "ID of the department to retrieve", required = true, example = "1")
           @PathVariable Long id) {
       return ok("Department fetched successfully!", departmentService.getDepartmentById(id));
   }

    @Operation(
            summary = SwaggerOperations.Department.GET_ALL_SUMMARY,
            description = SwaggerOperations.Department.GET_ALL_DESCRIPTION
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
                    responseCode = SwaggerResponses.CODE_500,
                    description = SwaggerResponses.SERVER_ERROR
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DepartmentResponse>>> getAllDepartments(
            @ParameterObject @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable) {
        return ok("Departments fetched successfully!", departmentService.getAllDepartments(pageable));
    }

    @Operation(
            summary = SwaggerOperations.Department.UPDATE_SUMMARY,
            description = SwaggerOperations.Department.UPDATE_DESCRIPTION
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_409,
                    description = "A department with the same name already exists"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @Parameter(description = "ID of the department to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest req) {
        return ok("Department updated successfully!", departmentService.updateDepartment(req, id));
    }

    @Operation(
            summary = SwaggerOperations.Department.PATCH_SUMMARY,
            description = SwaggerOperations.Department.PATCH_DESCRIPTION
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
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> patchDepartment(
            @Parameter(description = "ID of the department to partially update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PatchDepartmentRequest req) {
        return ok("Department updated successfully!", departmentService.patchDepartment(req, id));
    }



    @Operation(
            summary = SwaggerOperations.Department.DELETE_SUMMARY,
            description = SwaggerOperations.Department.DELETE_DESCRIPTION
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @Parameter(description = "ID of the department to delete", required = true, example = "1")
            @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ok("Department deleted successfully!", null);
    }



    @Operation(
            summary = SwaggerOperations.Department.FILTER_SUMMARY,
            description = SwaggerOperations.Department.FILTER_DESCRIPTION
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
            )
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<DepartmentFilterResponse>>> filterDepartment(
            @ParameterObject @ModelAttribute DepartmentFilterRequest req,
            @ParameterObject @PageableDefault(size = 10,page = 0,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
            ){
        return ok("Department filtered successfully!",departmentService.filterDepartment(req,pageable));
    }

    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
