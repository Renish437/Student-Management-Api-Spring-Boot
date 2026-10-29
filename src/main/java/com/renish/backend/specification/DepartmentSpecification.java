package com.renish.backend.specification;

import com.renish.backend.entity.Department;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSpecification {

    public static Specification<Department> filterDepartment(String keyword,String contact,String headOfDepartment,String location,String status){
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String search = "%" + keyword.trim().toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("departmentName")), search),

                                cb.like(cb.lower(root.get("code")), search)
                        )
                );
            }

            if (contact != null && !contact.isBlank()) {
                String search = contact.trim();

                predicates.add(
                        cb.or(
                                cb.equal(
                                        cb.lower(root.get("contactEmail")),
                                        search.toLowerCase()
                                ),
                                cb.equal(
                                        root.get("contactNumber"),
                                        search
                                )
                        )
                );
            }
          if(headOfDepartment!=null && !headOfDepartment.isBlank()){
              predicates.add(
                      cb.like(cb.lower(root.get("headOfDepartment")),"%"+headOfDepartment.trim().toLowerCase()+"%")

              );
          }
          if(location!=null && !location.isBlank()){
            predicates.add(
                 cb.like(cb.lower(root.get("location")),"%"+location.trim().toLowerCase()+"%")
           );
          }
          if(status!=null && !status.isBlank() && !status.equalsIgnoreCase("ALL")){
             predicates.add(
                     cb.equal(cb.lower(root.get("status")),status.trim().toLowerCase())
             );
          }
          return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
