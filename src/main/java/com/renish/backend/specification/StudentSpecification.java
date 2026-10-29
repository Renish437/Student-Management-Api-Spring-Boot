
package com.renish.backend.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;

public class StudentSpecification {

    public static Specification<Student> hasFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("firstName")),
                    "%" + firstName.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Student> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }

            return cb.equal(
                    cb.lower(root.get("email")),
                    email.trim().toLowerCase()
            );
        };
    }

    public static Specification<Student> hasCourseNameAndFee(
            String courseName, Double minFee, Double maxFee) {

        return (root, query, cb) -> {

            // Do not join courses if no course filter was supplied.
            if ((courseName == null || courseName.isBlank())
                    && minFee == null && maxFee == null) {
                return null;
            }

            Subquery<Long> subquery = query.subquery(Long.class);

            Root<Student> correlatedStudent = subquery.correlate(root);
            Join<Student, Course> course =
                    correlatedStudent.join("courses", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();

            if (courseName != null && !courseName.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(course.get("courseName")),
                        "%" + courseName.trim().toLowerCase() + "%"
                ));
            }

            if (minFee != null && maxFee != null) {
                predicates.add(cb.between(
                        course.get("fees"), minFee, maxFee
                ));
            } else if (minFee != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        course.get("fees"), minFee
                ));
            } else if (maxFee != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        course.get("fees"), maxFee
                ));
            }

            subquery.select(course.get("id"))
                    .where(predicates.toArray(new Predicate[0]));

            return cb.exists(subquery);
        };
    }

    public static Specification<Student> hasDepartmentName(
            String departmentName) {

        return (root, query, cb) -> {
            if (departmentName == null || departmentName.isBlank()) {
                return null;
            }

            Join<Student, Department> department =
                    root.join("department", JoinType.LEFT);

            String pattern =
                    "%" + departmentName.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(department.get("departmentName")), pattern),
                    cb.like(cb.lower(department.get("code")), pattern)
            );
        };
    }
}
