package com.renish.backend.controller;

import com.renish.backend.payload.ApiResponse;
import com.renish.backend.service.OtpService;
import com.renish.backend.swagger.SwaggerOperations;
import com.renish.backend.swagger.SwaggerResponses;
import com.renish.backend.swagger.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Tag(name = SwaggerTags.OTP, description = SwaggerTags.OTP_DESCRIPTION)
public class OtpController {
    private final OtpService otpService;

    @Operation(
            summary = SwaggerOperations.Otp.SEND_SUMMARY,
            description = SwaggerOperations.Otp.SEND_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = "OTP generated and emailed successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_500,
                    description = "OTP could not be generated or the email could not be sent"
            )
    })
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @Parameter(description = "Email address the OTP is sent to", required = true)
            @RequestParam String email){
        otpService.generateAndSendOtp(email);
        return ok("Otp send successfully!",null);
    }

    @Operation(
            summary = SwaggerOperations.Otp.VERIFY_SUMMARY,
            description = SwaggerOperations.Otp.VERIFY_DESCRIPTION
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_200,
                    description = "OTP verified successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_400,
                    description = SwaggerResponses.OTP_INVALID
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_404,
                    description = SwaggerResponses.OTP_NOT_FOUND
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_409,
                    description = SwaggerResponses.OTP_CONFLICT
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = SwaggerResponses.CODE_429,
                    description = SwaggerResponses.OTP_ATTEMPTS_EXCEEDED
            )
    })
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Parameter(description = "Email address the OTP was requested for", required = true)
            @RequestParam String email,
            @Parameter(description = "6-digit one-time password received by email", required = true)
            @RequestParam String otp
    ){
        otpService.verifyOtp(email,otp);
        return ok("Otp verified successfully!",null);
    }

    private static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), message, data));
    }
    private static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), message, data));
    }
}
