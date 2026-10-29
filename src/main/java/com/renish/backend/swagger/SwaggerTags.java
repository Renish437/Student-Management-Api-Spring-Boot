package com.renish.backend.swagger;

/**
 * Central registry of Swagger/OpenAPI tag names and descriptions.
 *
 * Every controller references these constants through {@code @Tag(name = SwaggerTags.X, description = SwaggerTags.X_DESCRIPTION)}.
 * Change a value here and every related endpoint in Swagger UI is updated at once.
 */
public final class SwaggerTags {

    private SwaggerTags() {
    }

    // ------------------------------------------------------------------
    // Public / user facing APIs
    // ------------------------------------------------------------------

    public static final String AUTH = "1. Auth APIs";
    public static final String AUTH_DESCRIPTION =
            "Authentication endpoints: login, refresh token and logout (no access token required)";


    public static final String OTP = "9. OTP APIs";
    public static final String OTP_DESCRIPTION =
            "One-time password APIs: send and verify email OTPs for account verification";

    public static final String STUDENTS = "2. Student APIs";
    public static final String STUDENTS_DESCRIPTION =
            "Student self-management APIs: view, create, update, delete, sort and filter students";

    public static final String COURSES = "3. Course APIs";
    public static final String COURSES_DESCRIPTION =
            "Course APIs: create, view, update, delete and filter courses";

    public static final String DEPARTMENTS = "4. Department APIs";
    public static final String DEPARTMENTS_DESCRIPTION =
            "Department APIs: create, view, update, delete and filter departments";

    // ------------------------------------------------------------------
    // Admin APIs
    // ------------------------------------------------------------------

    public static final String ADMIN_STUDENTS = "5. Admin Student APIs";
    public static final String ADMIN_STUDENTS_DESCRIPTION =
            "Admin student management: search with soft-deleted records, bulk create, soft delete, restore and hard delete";

    public static final String ADMIN_COURSES = "6. Admin Course APIs";
    public static final String ADMIN_COURSES_DESCRIPTION =
            "Admin course management: search, bulk create, status change, soft delete, restore and hard delete";

    public static final String ADMIN_DEPARTMENTS = "7. Admin Department APIs";
    public static final String ADMIN_DEPARTMENTS_DESCRIPTION =
            "Admin department management: search, bulk create, status change, soft delete, restore and hard delete";


    public static final String HOME = "8. Home APIs";
    public static final String HOME_DESCRIPTION = "Public landing page of the application";

}
