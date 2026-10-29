package com.renish.backend.service;

import com.renish.backend.dto.request.DepartmentRequest;
import com.renish.backend.dto.request.admin.AdminDepartmentFilterRequest;
import com.renish.backend.dto.response.DepartmentResponse;
import com.renish.backend.dto.response.PageResponse;
import com.renish.backend.dto.response.admin.AdminDepartmentResponse;
import com.renish.backend.entity.Department;
import com.renish.backend.exception.ResourceNotFoundException;
import com.renish.backend.repository.DepartmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the caching itself, which the behaviour tests deliberately turn off:
 * a read that goes straight to the repository (so no eviction runs) must be
 * served from Redis, and the service write must evict it.
 *
 * <p>H2 is pointed at its own database and Redis entries are cleared around
 * every test so results do not depend on another suite's leftovers.
 */
@SpringBootTest(properties = {
        "spring.cache.type=redis",
        "spring.datasource.url=jdbc:h2:mem:cachedb;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles("test")
class CacheBehaviorIntegrationTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private AdminDepartmentService adminDepartmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    private final PageRequest pageRequest = PageRequest.of(0, 100);
    private final AdminDepartmentFilterRequest adminFilter = new AdminDepartmentFilterRequest();

    @BeforeEach
    void reset() {
        clearCaches();
        jdbcTemplate.execute("DELETE FROM departments");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM departments");
        clearCaches();
    }

    @Test
    void departmentListIsServedFromRedisUntilAServiceWriteEvictsIt() {
        long total = departmentService.getAllDepartments(pageRequest).totalElements();
        // prime the admin cache too, otherwise its first read would miss by design
        assertEquals(total, adminDepartmentService.getAllDepartments(adminFilter, pageRequest).totalElements());

        // repository write bypasses the service, so nothing evicts the caches
        departmentRepository.save(Department.builder()
                .departmentName("Written-Bypass-" + System.nanoTime())
                .build());

        // both still answer from Redis: the new row is invisible
        assertEquals(total, departmentService.getAllDepartments(pageRequest).totalElements());
        assertEquals(total, adminDepartmentService.getAllDepartments(adminFilter, pageRequest).totalElements());

        // a service write evicts both caches, so the row shows up
        departmentService.createDepartment(DepartmentRequest.builder()
                .departmentName("Evict-Trigger-" + System.nanoTime())
                .code("ET")
                .build());

        assertTrue(departmentService.getAllDepartments(pageRequest).totalElements() > total);
        assertTrue(adminDepartmentService.getAllDepartments(adminFilter, pageRequest).totalElements() > total);
    }

    @Test
    void departmentByIdIsServedFromRedisUntilAServiceWriteEvictsIt() {
        DepartmentResponse created = departmentService.createDepartment(
                DepartmentRequest.builder()
                        .departmentName("ById-Cache-" + System.nanoTime())
                        .code("BC")
                        .build());

        DepartmentResponse firstRead = departmentService.getDepartmentById(created.getId());
        assertEquals(created.getDepartmentName(), firstRead.getDepartmentName());

        Department entity = departmentRepository.findById(created.getId()).orElseThrow();
        entity.setDepartmentName("Renamed-Bypass");
        departmentRepository.save(entity);

        // still the old value: the read came from Redis
        assertEquals(created.getDepartmentName(),
                departmentService.getDepartmentById(created.getId()).getDepartmentName());

        // soft delete evicts, so the next read hits the database and misses
        departmentService.deleteDepartment(created.getId());
        assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentById(created.getId()));
    }

    private void clearCaches() {
        for (String name : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}