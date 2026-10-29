package com.renish.backend.swagger;

/**
 * Central registry of HTTP status codes and response descriptions used by
 * {@code @ApiResponse} / {@code @ApiResponses} annotations.
 *
 * Grouped per concern so a wording or code change only has to happen once:
 * every endpoint referencing {@code SwaggerResponses.CODE_404} +
 * {@code SwaggerResponses.NOT_FOUND} is refreshed together.
 */
public final class SwaggerResponses {

    private SwaggerResponses() {
    }

    // ------------------------------------------------------------------
    // Status codes
    // ------------------------------------------------------------------

    public static final String CODE_200 = "200";
    public static final String CODE_201 = "201";
    public static final String CODE_204 = "204";
    public static final String CODE_400 = "400";
    public static final String CODE_401 = "401";
    public static final String CODE_403 = "403";
    public static final String CODE_404 = "404";
    public static final String CODE_409 = "409";
    public static final String CODE_429 = "429";
    public static final String CODE_500 = "500";

    // ------------------------------------------------------------------
    // Success descriptions
    // ------------------------------------------------------------------

    public static final String OK = "Request processed successfully";
    public static final String CREATED = "Resource created successfully";
    public static final String NO_CONTENT = "Resource deleted successfully";

    // ------------------------------------------------------------------
    // Error descriptions
    // ------------------------------------------------------------------

    public static final String BAD_REQUEST =
            "Validation failed or malformed request payload";
    public static final String UNAUTHORIZED =
            "Access token is missing, expired, revoked or invalid";
    public static final String FORBIDDEN =
            "Authenticated user does not have the required role";
    public static final String NOT_FOUND =
            "Requested resource does not exist";
    public static final String CONFLICT =
            "Resource already exists or conflicts with the current state";
    public static final String SERVER_ERROR =
            "Unexpected server error";

    // ------------------------------------------------------------------
    // Authentication specific descriptions
    // ------------------------------------------------------------------

    public static final String INVALID_CREDENTIALS =
            "Invalid email or password";
    public static final String INVALID_REFRESH_TOKEN =
            "Refresh token is invalid, expired or revoked";

    // ------------------------------------------------------------------
    // OTP specific descriptions
    // ------------------------------------------------------------------

    public static final String OTP_INVALID =
            "OTP is incorrect; the attempt is counted (max 3 attempts)";
    public static final String OTP_NOT_FOUND =
            "No OTP was requested for this email address";
    public static final String OTP_CONFLICT =
            "OTP has expired or was already verified";
    public static final String OTP_ATTEMPTS_EXCEEDED =
            "Maximum OTP attempts exceeded; request a new OTP";

    // ------------------------------------------------------------------
    // Profile image specific descriptions
    // ------------------------------------------------------------------

    public static final String IMAGE_UPLOADED =
            "Profile image uploaded successfully";
    public static final String IMAGE_INVALID_FILE =
            "Invalid file: unsupported file type or file size exceeds the limit";
    public static final String IMAGE_STUDENT_NOT_FOUND =
            "Student not found";
    public static final String IMAGE_DOWNLOADED =
            "Profile image downloaded successfully";
    public static final String IMAGE_NOT_FOUND =
            "Student or profile image not found";
    public static final String IMAGE_DOWNLOAD_ERROR =
            "Internal server error while downloading the profile image";
}
