package com.renish.backend.controller;

import com.renish.backend.dto.request.LoginRequest;
import com.renish.backend.dto.response.LoginResponse;
import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.AuthService;
import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.AUTH, description = SwaggerTags.AUTH_DESCRIPTION)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = SwaggerOperations.Auth.LOGIN_SUMMARY,
            description = SwaggerOperations.Auth.LOGIN_DESCRIPTION
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
                    description = SwaggerResponses.INVALID_CREDENTIALS
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest req
            ){
        return ok("Account Login successfully!",authService.login(req));
    }

    @Operation(
            summary = SwaggerOperations.Auth.REFRESH_SUMMARY,
            description = SwaggerOperations.Auth.REFRESH_DESCRIPTION
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
                    description = SwaggerResponses.INVALID_REFRESH_TOKEN
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestParam String refreshToken
    ){
        return ok("Token refreshed successfully!",authService.refreshToken(refreshToken));
    }

    @Operation(
            summary = SwaggerOperations.Auth.LOGOUT_SUMMARY,
            description = SwaggerOperations.Auth.LOGOUT_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = SwaggerResponses.NO_CONTENT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_401,
                    description = SwaggerResponses.INVALID_REFRESH_TOKEN
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestParam String refreshToken
    ){
        authService.logout(refreshToken);
        return ok("Account logout successfully!",null);
    }

    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }

    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }

}
