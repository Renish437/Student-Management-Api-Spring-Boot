package com.renish.backend.repository.custom;

import com.renish.backend.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StudentRepositoryCustomImpl implements StudentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Student> findStudentCustom() {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s",Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findStudentByDepartment(String departmentName) {
        TypedQuery<Student> students = entityManager.createQuery("SELECT s FROM Student s " +
                "JOIN s.department d WHERE d.departmentName = :departmentName", Student.class);
        students.setParameter("departmentName",departmentName);
        return students.getResultList();
    }

    @Override
    public Page<Student> findAllWithRelations(
            Specification<Student> specification,
            Pageable pageable) {

        // 1. Query paginated student IDs

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> idQuery = cb.createTupleQuery();
        Root<Student> root = idQuery.from(Student.class);

        Predicate predicate = specification.toPredicate(root, idQuery, cb);

// Select ID plus every field used for sorting
        List<jakarta.persistence.criteria.Selection<?>> selections =
                new ArrayList<>();

        selections.add(root.get("id").alias("id"));

        List<Order> orders = new ArrayList<>();

        for (Sort.Order sort : pageable.getSort()) {
            var path = root.get(sort.getProperty());

            selections.add(path.alias(sort.getProperty()));

            orders.add(sort.isAscending()
                    ? cb.asc(path)
                    : cb.desc(path));
        }

        idQuery.multiselect(selections)
                .distinct(true);

        if (predicate != null) {
            idQuery.where(predicate);
        }

        if (!orders.isEmpty()) {
            idQuery.orderBy(orders);
        }

        List<Tuple> tuples = entityManager.createQuery(idQuery)
                .setFirstResult(Math.toIntExact(pageable.getOffset()))
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<Long> ids = tuples.stream()
                .map(tuple -> tuple.get("id", Long.class))
                .toList();


        // 2. Count matching students
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Student> countRoot = countQuery.from(Student.class);

        Predicate countPredicate =
                specification.toPredicate(countRoot, countQuery, cb);

        countQuery.select(cb.countDistinct(countRoot));

        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }

        Long total = entityManager.createQuery(countQuery)
                .getSingleResult();

        // 3. No IDs means no fetch query is necessary
        if (ids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        // 4. Fetch relations for the current page only

        CriteriaQuery<Student> fetchQuery =
                cb.createQuery(Student.class);

        Root<Student> fetchRoot = fetchQuery.from(Student.class);

        fetchRoot.fetch("department", JoinType.LEFT);
        fetchRoot.fetch("courses", JoinType.LEFT);
        fetchRoot.fetch("address", JoinType.LEFT);

        fetchQuery.select(fetchRoot)
                .distinct(true)
                .where(fetchRoot.get("id").in(ids));

        List<Student> fetchedStudents = entityManager
                .createQuery(fetchQuery)
                .getResultList();


        // 5. Preserve the order from the paginated ID query
        Map<Long, Student> studentMap = fetchedStudents.stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        Function.identity(),
                        (first, second) -> first,
                        HashMap::new));

        List<Student> orderedStudents = ids.stream()
                .map(studentMap::get)
                .filter(java.util.Objects::nonNull)
                .toList();

        return new PageImpl<>(orderedStudents, pageable, total);
    }
}
