package com.renish.backend.swagger;

/**
 * Central registry of Swagger/OpenAPI operation summaries and descriptions.
 *
 * Every controller method references these constants through
 * {@code @Operation(summary = SwaggerOperations.<Group>.X_SUMMARY, description = SwaggerOperations.<Group>.X_DESCRIPTION)}.
 *
 * One nested class per API group, mirroring the controllers:
 * edit a text here and Swagger UI is updated for that endpoint everywhere.
 */
public final class SwaggerOperations {

    private SwaggerOperations() {
    }

    // ==================================================================
    // HomeController
    // ==================================================================
    public static final class Home {

        private Home() {
        }

        public static final String HOME_SUMMARY = "Home page";
        public static final String HOME_DESCRIPTION =
                "Returns a plain welcome message for the application home page.";
    }

    // ==================================================================
    // AuthController
    // ==================================================================
    public static final class Auth {

        private Auth() {
        }

        public static final String LOGIN_SUMMARY = "Login";
        public static final String LOGIN_DESCRIPTION =
                "Authenticates a user with email and password and returns a JWT access token "
                        + "together with a refresh token.";

        public static final String REFRESH_SUMMARY = "Refresh access token";
        public static final String REFRESH_DESCRIPTION =
                "Exchanges a valid refresh token for a new access token / refresh token pair.";

        public static final String LOGOUT_SUMMARY = "Logout";
        public static final String LOGOUT_DESCRIPTION =
                "Invalidates the given refresh token so it can no longer be used to obtain new tokens.";
    }

    // ==================================================================
    // StudentController
    // ==================================================================
    public static final class Student {

        private Student() {
        }

        public static final String GET_ALL_SUMMARY = "Get all students";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a page of active students, newest first by default.";

        public static final String GET_PAGED_SUMMARY = "Get students by page number";
        public static final String GET_PAGED_DESCRIPTION =
                "Retrieves students using explicit page and size values instead of Spring's Pageable defaults.";

        public static final String GET_PROJECTION_SUMMARY = "Get students as projection DTO";
        public static final String GET_PROJECTION_DESCRIPTION =
                "Retrieves a lightweight projection of students containing only the fields needed for list views.";

        public static final String GET_BY_ID_SUMMARY = "Get student by ID";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single student, including address, department and enrolled courses, by its ID.";

        public static final String CREATE_SUMMARY = "Create student";
        public static final String CREATE_DESCRIPTION =
                "Creates a new student with address, department and course enrolments. "
                        + "Requires the ADMIN role.";

        public static final String UPDATE_SUMMARY = "Update student";
        public static final String UPDATE_DESCRIPTION =
                "Fully updates an existing student. All required fields must be supplied. Requires the ADMIN role.";

        public static final String PATCH_SUMMARY = "Partially update student";
        public static final String PATCH_DESCRIPTION =
                "Updates only the provided fields of an existing student. Requires the ADMIN role.";

        public static final String DELETE_SUMMARY = "Delete student";
        public static final String DELETE_DESCRIPTION =
                "Deletes the student with the given ID. Requires the ADMIN role.";

        public static final String SORT_SUMMARY = "Get students sorted by first name";
        public static final String SORT_DESCRIPTION =
                "Retrieves all students sorted alphabetically by first name.";

        public static final String FILTER_SUMMARY = "Filter students";
        public static final String FILTER_DESCRIPTION =
                "Dynamically filters students by first name, email, course name, department name and course fee range. "
                        + "Every filter is optional; omitted filters are ignored.";

        public static final String FILTER_BY_TOTAL_FEES_SUMMARY = "Filter students by total fees";
        public static final String FILTER_BY_TOTAL_FEES_DESCRIPTION =
                "Retrieves a page of students whose courses fall inside the given total fee range (minFee to maxFee).";

        public static final String UPLOAD_IMAGE_SUMMARY = "Upload student profile image";
        public static final String UPLOAD_IMAGE_DESCRIPTION =
                "Uploads a JPG, PNG or WEBP profile image for the student with the given ID. "
                        + "Requires the ADMIN role.";

        public static final String DOWNLOAD_IMAGE_SUMMARY = "Download student profile image";
        public static final String DOWNLOAD_IMAGE_DESCRIPTION =
                "Downloads the stored profile image of the student with the given ID as an attachment.";
    }

    // ==================================================================
    // OtpController
    // ==================================================================
    public static final class Otp {

        private Otp() {
        }

        public static final String SEND_SUMMARY = "Send OTP";
        public static final String SEND_DESCRIPTION =
                "Generates a 6-digit OTP that is valid for 5 minutes, stores it hashed and emails it "
                        + "to the given address.";

        public static final String VERIFY_SUMMARY = "Verify OTP";
        public static final String VERIFY_DESCRIPTION =
                "Verifies the most recent OTP for the given email address. At most 3 attempts are allowed "
                        + "before the OTP must be requested again.";
    }

    // ==================================================================
    // CourseController
    // ==================================================================
    public static final class Course {

        private Course() {
        }

        public static final String CREATE_SUMMARY = "Create course";
        public static final String CREATE_DESCRIPTION =
                "Creates a new course, optionally assigning it to a department.";

        public static final String GET_ALL_SUMMARY = "Get all courses";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a page of active courses, newest first by default.";

        public static final String GET_BY_ID_SUMMARY = "Get course by ID";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single course by its ID.";

        public static final String UPDATE_SUMMARY = "Update course";
        public static final String UPDATE_DESCRIPTION =
                "Fully updates an existing course. All required fields must be supplied.";

        public static final String PATCH_SUMMARY = "Partially update course";
        public static final String PATCH_DESCRIPTION =
                "Updates only the provided fields of an existing course.";

        public static final String DELETE_SUMMARY = "Delete course";
        public static final String DELETE_DESCRIPTION =
                "Soft deletes the course with the given ID.";

        public static final String FILTER_SUMMARY = "Filter courses";
        public static final String FILTER_DESCRIPTION =
                "Dynamically filters courses with optional attributes and returns a lightweight course summary.";
    }

    // ==================================================================
    // DepartmentController
    // ==================================================================
    public static final class Department {

        private Department() {
        }

        public static final String CREATE_SUMMARY = "Create department";
        public static final String CREATE_DESCRIPTION =
                "Creates a new department.";

        public static final String GET_ALL_SUMMARY = "Get all departments";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a page of active departments, newest first by default.";

        public static final String GET_BY_ID_SUMMARY = "Get department by ID";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single department, including its courses, by its ID.";

        public static final String UPDATE_SUMMARY = "Update department";
        public static final String UPDATE_DESCRIPTION =
                "Fully updates an existing department. All required fields must be supplied.";

        public static final String PATCH_SUMMARY = "Partially update department";
        public static final String PATCH_DESCRIPTION =
                "Updates only the provided fields of an existing department.";

        public static final String DELETE_SUMMARY = "Delete department";
        public static final String DELETE_DESCRIPTION =
                "Soft deletes the department with the given ID.";

        public static final String FILTER_SUMMARY = "Filter departments";
        public static final String FILTER_DESCRIPTION =
                "Dynamically filters departments with optional attributes and returns a lightweight department summary.";
    }

    // ==================================================================
    // AdminStudentController
    // ==================================================================
    public static final class AdminStudent {

        private AdminStudent() {
        }

        public static final String GET_ALL_SUMMARY = "Get all students (admin)";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a filtered, paginated list of students including soft-deleted records. "
                        + "Supports filtering by name, email, course, department, fee range and status (ALL, ACTIVE, DELETED).";

        public static final String GET_BY_ID_SUMMARY = "Get student by ID (admin)";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single student, including soft-deleted ones, by its ID.";

        public static final String RESTORE_SUMMARY = "Restore soft-deleted student";
        public static final String RESTORE_DESCRIPTION =
                "Restores a previously soft-deleted student so it becomes active again.";

        public static final String BULK_CREATE_SUMMARY = "Bulk create students";
        public static final String BULK_CREATE_DESCRIPTION =
                "Creates several students in a single request. Each element is validated individually.";

        public static final String BULK_DELETE_SUMMARY = "Bulk soft delete students";
        public static final String BULK_DELETE_DESCRIPTION =
                "Soft deletes the students with the given IDs. Records remain restorable.";

        public static final String HARD_DELETE_SUMMARY = "Hard delete student";
        public static final String HARD_DELETE_DESCRIPTION =
                "Permanently removes the student with the given ID. This action cannot be undone.";

        public static final String BULK_HARD_DELETE_SUMMARY = "Bulk hard delete students";
        public static final String BULK_HARD_DELETE_DESCRIPTION =
                "Permanently removes the students with the given IDs. This action cannot be undone.";
    }

    // ==================================================================
    // AdminCourseController
    // ==================================================================
    public static final class AdminCourse {

        private AdminCourse() {
        }

        public static final String GET_ALL_SUMMARY = "Get all courses (admin)";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a filtered, paginated list of courses including soft-deleted records.";

        public static final String BULK_CREATE_SUMMARY = "Bulk create courses";
        public static final String BULK_CREATE_DESCRIPTION =
                "Creates several courses in a single request. Each element is validated individually.";

        public static final String UPDATE_STATUS_SUMMARY = "Update course status";
        public static final String UPDATE_STATUS_DESCRIPTION =
                "Changes the status (for example ACTIVE / INACTIVE) of the course with the given ID.";

        public static final String GET_BY_ID_SUMMARY = "Get course by ID (admin)";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single course, including soft-deleted ones, by its ID.";

        public static final String RESTORE_SUMMARY = "Restore soft-deleted course";
        public static final String RESTORE_DESCRIPTION =
                "Restores a previously soft-deleted course so it becomes active again.";

        public static final String BULK_DELETE_SUMMARY = "Bulk soft delete courses";
        public static final String BULK_DELETE_DESCRIPTION =
                "Soft deletes the courses with the given IDs. Records remain restorable.";

        public static final String HARD_DELETE_SUMMARY = "Hard delete course";
        public static final String HARD_DELETE_DESCRIPTION =
                "Permanently removes the course with the given ID. This action cannot be undone.";

        public static final String BULK_HARD_DELETE_SUMMARY = "Bulk hard delete courses";
        public static final String BULK_HARD_DELETE_DESCRIPTION =
                "Permanently removes the courses with the given IDs. This action cannot be undone.";
    }

    // ==================================================================
    // AdminDepartmentController
    // ==================================================================
    public static final class AdminDepartment {

        private AdminDepartment() {
        }

        public static final String GET_ALL_SUMMARY = "Get all departments (admin)";
        public static final String GET_ALL_DESCRIPTION =
                "Retrieves a filtered, paginated list of departments including soft-deleted records.";

        public static final String GET_BY_ID_SUMMARY = "Get department by ID (admin)";
        public static final String GET_BY_ID_DESCRIPTION =
                "Retrieves a single department, including soft-deleted ones, by its ID.";

        public static final String RESTORE_SUMMARY = "Restore soft-deleted department";
        public static final String RESTORE_DESCRIPTION =
                "Restores a previously soft-deleted department so it becomes active again.";

        public static final String BULK_CREATE_SUMMARY = "Bulk create departments";
        public static final String BULK_CREATE_DESCRIPTION =
                "Creates several departments in a single request. Each element is validated individually.";

        public static final String UPDATE_STATUS_SUMMARY = "Update department status";
        public static final String UPDATE_STATUS_DESCRIPTION =
                "Changes the status of the department with the given ID.";

        public static final String BULK_DELETE_SUMMARY = "Bulk soft delete departments";
        public static final String BULK_DELETE_DESCRIPTION =
                "Soft deletes the departments with the given IDs. Records remain restorable.";

        public static final String HARD_DELETE_SUMMARY = "Hard delete department";
        public static final String HARD_DELETE_DESCRIPTION =
                "Permanently removes the department with the given ID. This action cannot be undone.";

        public static final String BULK_HARD_DELETE_SUMMARY = "Bulk hard delete departments";
        public static final String BULK_HARD_DELETE_DESCRIPTION =
                "Permanently removes the departments with the given IDs. This action cannot be undone.";
    }
}
