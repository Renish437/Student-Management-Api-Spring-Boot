CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6),
    deleted_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(255),
    description VARCHAR(255),
    head_of_department VARCHAR(255),
    location VARCHAR(255),
    contact_email VARCHAR(255),
    contact_number VARCHAR(255),
    status VARCHAR(100) DEFAULT 'ACTIVE'
);

CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6),
    deleted_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    course_name VARCHAR(255) NOT NULL,
    duration VARCHAR(255),
    fees DOUBLE,
    instructor_name VARCHAR(255),
    status VARCHAR(255) DEFAULT 'DRAFT',
    department_id BIGINT NOT NULL,
    CONSTRAINT fk_course_department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT uk_course_name_department UNIQUE (course_name, department_id)
);

CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME(6),
    deleted_by VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_image VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255) DEFAULT 'USER',
    address_id BIGINT,
    department_id BIGINT,
    CONSTRAINT fk_student_address FOREIGN KEY (address_id) REFERENCES addresses(id),
    CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE student_courses (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_student_courses_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_student_courses_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE otp_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    otp VARCHAR(255),
    expires_at DATETIME(6),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INT DEFAULT 0
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_refresh_token_student FOREIGN KEY (student_id) REFERENCES students(id)
);
