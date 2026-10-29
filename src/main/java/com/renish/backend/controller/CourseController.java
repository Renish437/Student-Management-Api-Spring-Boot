package com.renish.backend.controller;

import com.renish.backend.dto.request.*;
import com.renish.backend.dto.response.CourseFilterResponse;
import com.renish.backend.dto.response.CourseResponse;
import com.renish.backend.dto.response.CourseStatusResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = SwaggerTags.COURSES, description = SwaggerTags.COURSES_DESCRIPTION)
public class CourseController {
private final CourseService courseService;

    @Operation(
            summary = SwaggerOperations.Course.CREATE_SUMMARY,
            description = SwaggerOperations.Course.CREATE_DESCRIPTION
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
                    description = "A course with the same name already exists"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest req){

        return created("Course created successfully!",courseService.createCourse(req));
    }


    @Operation(
            summary = SwaggerOperations.Course.GET_ALL_SUMMARY,
            description = SwaggerOperations.Course.GET_ALL_DESCRIPTION
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
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getAllCourses(
            @ParameterObject @PageableDefault(size = 10,page = 0,direction = Sort.Direction.DESC,sort = "createdAt")
            Pageable pageable
    ){
        return ok("Courses fetched successfully!",courseService.getAllCourses(pageable));
    }

    @Operation(
            summary = SwaggerOperations.Course.GET_BY_ID_SUMMARY,
            description = SwaggerOperations.Course.GET_BY_ID_DESCRIPTION
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
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(
            @Parameter(description = "ID of the course to retrieve", required = true, example = "1")
            @PathVariable Long id
    ){
        return ok("Course fetched successfully!",courseService.getCourseById(id));
    }

    @Operation(
            summary = SwaggerOperations.Course.UPDATE_SUMMARY,
            description = SwaggerOperations.Course.UPDATE_DESCRIPTION
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
                    description = "A course with the same name already exists"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @Parameter(description = "ID of the course to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest req
    ){
        return ok("Course updated successfully!",courseService.updateCourse(req,id));
    }

    @Operation(
            summary = SwaggerOperations.Course.PATCH_SUMMARY,
            description = SwaggerOperations.Course.PATCH_DESCRIPTION
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
    public ResponseEntity<ApiResponse<CourseResponse>> patchCourse(
            @Parameter(description = "ID of the course to partially update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PatchCourseRequest req
    ){
        return ok("Course updated successfully!",courseService.patchCourse(req,id));
    }



    @Operation(
            summary = SwaggerOperations.Course.DELETE_SUMMARY,
            description = SwaggerOperations.Course.DELETE_DESCRIPTION
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
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @Parameter(description = "ID of the course to delete", required = true, example = "1")
            @PathVariable Long id
    ){
        courseService.deleteCourse(id);
        return  ok("Course deleted successfully!",null);
    }


    @Operation(
            summary = SwaggerOperations.Course.FILTER_SUMMARY,
            description = SwaggerOperations.Course.FILTER_DESCRIPTION
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
    public ResponseEntity<ApiResponse<PageResponse<CourseFilterResponse>>> filterCourse(
       @ParameterObject  @ModelAttribute  CourseFilterRequest req,
        @ParameterObject @PageableDefault(page = 0,size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ok("Course filtered successfully!", courseService.filterCourse(req,pageable));
    }

    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
