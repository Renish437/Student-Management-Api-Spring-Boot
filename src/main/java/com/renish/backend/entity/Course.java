package com.renish.backend.entity;


import com.renish.backend.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;




@Getter
@Setter
@Entity
@Table(name = "courses", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_course_name_department",
                columnNames = {"course_name", "department_id"}
        )
})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = """
    UPDATE courses
    SET deleted = true
    WHERE id = ?
    """)
@FilterDef(name = "deletedFilter", autoEnabled = true)
@Filter(name = "deletedFilter", condition = "deleted = false")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName;

    private String duration;

    private Double fees;

    private String instructorName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",nullable = false)
    private Department department;  // owning side

    @Builder.Default
    @ManyToMany(mappedBy = "courses",fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();  // inverse side


}
