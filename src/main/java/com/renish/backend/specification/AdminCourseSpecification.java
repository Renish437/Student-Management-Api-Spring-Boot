package com.renish.backend.specification;

import com.renish.backend.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminCourseSpecification {

    public static Specification<Course> filterCourse(String courseName, String duration, String instructorName,
                                                     String departmentName, Double minFee, Double maxFee, String status,boolean deleted) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (courseName != null && !courseName.isBlank()) {

                predicates.add(
                        cb.like(cb.lower(root.get("courseName")), "%" + courseName.trim().toLowerCase() + "%"));
            }
            if (duration != null && !duration.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("duration")), "%" + duration.trim().toLowerCase() + "%"));
            }
            if (instructorName != null && !instructorName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("instructorName")), "%" + instructorName.trim().toLowerCase() + "%"));
            }
            if (departmentName != null && !departmentName.isBlank()) {
                predicates.add(
                        cb.or(cb.like(cb.lower(root.join("department").get("departmentName")), "%" + departmentName.trim().toLowerCase() + "%"),
                                cb.like(cb.lower(root.join("department").get("code")), "%" + departmentName.trim().toLowerCase() + "%")));
            }
            if(minFee!=null && maxFee!=null){
                predicates.add(
                        cb.between(root.get("fees"), minFee, maxFee)
                );
            }else if(minFee!=null){
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("fees"), minFee)
                );
            }else if(maxFee!=null){
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("fees"), maxFee)
                );
            }

            if(status!=null && !status.isBlank() && !status.equalsIgnoreCase("ALL")){
                predicates.add(
                        cb.equal(cb.lower(root.get("status")),status.trim().toLowerCase())
                );

            }
            if (deleted) {
                predicates.add(cb.isTrue(root.get("deleted")));
            } else if (!deleted) {
                predicates.add(cb.isFalse(root.get("deleted")));
            }


            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
