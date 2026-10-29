package com.renish.backend.repository.custom;

import com.renish.backend.entity.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryCustomImpl
        implements CourseRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<Course> findAllWithDepartment(
            Specification<Course> specification,
            Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Course> query =
                cb.createQuery(Course.class);

        Root<Course> root = query.from(Course.class);

        // Fetch department with the course
        root.fetch("department", JoinType.LEFT);

        Predicate predicate = specification.toPredicate(
                root, query, cb);

        if (predicate != null) {
            query.where(predicate);
        }

        // Apply sorting
        List<Order> orders = new ArrayList<>();

        pageable.getSort().forEach(sort -> {
            Path<?> path = root.get(sort.getProperty());

            orders.add(sort.isAscending()
                    ? cb.asc(path)
                    : cb.desc(path));
        });

        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }

        List<Course> courses = entityManager
                .createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query without fetch join
        CriteriaQuery<Long> countQuery =
                cb.createQuery(Long.class);

        Root<Course> countRoot =
                countQuery.from(Course.class);

        Predicate countPredicate = specification.toPredicate(
                countRoot, countQuery, cb);

        countQuery.select(cb.count(countRoot));

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        Long total = entityManager
                .createQuery(countQuery)
                .getSingleResult();

        return new PageImpl<>(courses, pageable, total);
    }
}
