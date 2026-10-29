package com.renish.backend.specification;

import com.renish.backend.entity.Course;
import com.renish.backend.entity.Department;
import com.renish.backend.entity.Student;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminStudentSpecification {

    public static Specification<Student>  filterStudents(String firstName,String lastName,String email,String courseName, Double minFee, Double maxFee,String departmentName,boolean deleted){
        return (root,query,cb)->{

            List<Predicate> predicates = new ArrayList<>();

            if(firstName!=null && !firstName.isBlank()){
                predicates.add(
                        cb.like(cb.lower(root.get("firstName")),"%"+firstName.trim().toLowerCase()+"%")
                );
            }
            if(lastName!=null && !lastName.isBlank()){
                predicates.add(
                        cb.like(cb.lower(root.get("lastName")),"%"+lastName.trim().toLowerCase()+"%")
                );
            }
            if(email!=null && !email.isBlank()){
                predicates.add(
                        cb.equal(cb.lower(root.get("email")),email.trim().toLowerCase())
                );
            }

if((courseName!=null && courseName.isBlank())|| maxFee!=null || minFee!=null){
    Subquery<Long> subquery = query.subquery(Long.class);

    Root<Student> correlatedStudent = subquery.correlate(root);
    Join<Student, Course> course =
            correlatedStudent.join("courses", JoinType.INNER);


    if(courseName!=null && !courseName.isBlank()){

        predicates.add(
                cb.like(cb.lower(course.get("courseName")),"%"+courseName.trim().toLowerCase()+"%")
        );
    }
    if(minFee!=null && maxFee!=null){
        predicates.add(
                cb.between(course.get("fees"),minFee,maxFee)
        );
    }
    if(minFee!=null){
        predicates.add(
                cb.greaterThanOrEqualTo(course.get("fees"),minFee)
        );
    }
    if(maxFee!=null){
        predicates.add(
                cb.lessThanOrEqualTo(course.get("fees"),maxFee)
        );
    }

}

if(departmentName!=null && !departmentName.isBlank()){
    Join<Student,Department> departmentJoin = root.join("department",JoinType.LEFT);
    predicates.add(
        cb.or(
                cb.like(cb.lower(departmentJoin.get("departmentName")),"%"+departmentName.trim().toLowerCase()+"%"),
                cb.like(cb.lower(departmentJoin.get("departmentName")),"%"+departmentName.trim().toLowerCase()+"%")
        )
    );

}
            if (deleted) {
                predicates.add(cb.isTrue(root.get("deleted")));
            } else if (!deleted) {
                predicates.add(cb.isFalse(root.get("deleted")));
            }


            return cb.and(
                    predicates.toArray(Predicate[]::new)
            );

        };

    }

}
