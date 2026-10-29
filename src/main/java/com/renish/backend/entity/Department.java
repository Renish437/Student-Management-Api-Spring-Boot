package com.renish.backend.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import com.renish.backend.enums.DepartmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@Entity
@Table(name = "departments")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = """
    UPDATE departments
    SET deleted = true
    WHERE id = ?
    """)
@FilterDef(name = "deletedFilter", autoEnabled = true)
@Filter(name = "deletedFilter", condition = "deleted = false")
public class Department extends BaseEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100,unique = true)
    private String departmentName;

    private String code;

    private String description;

    private String headOfDepartment;

    private String location;

    private String contactEmail;

    private String contactNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private DepartmentStatus status=DepartmentStatus.ACTIVE;

    @OneToMany(mappedBy = "department")   // default: fetch = FetchType.LAZY
    @Builder.Default
    private Set<Student> students =  new HashSet<>();

   @OneToMany(mappedBy = "department")    // default: fetch = FetchType.LAZY
   @Builder.Default
   private Set<Course> courses =  new HashSet<>();;    // inverse side




}
