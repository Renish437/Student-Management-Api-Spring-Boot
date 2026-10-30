package com.renish.backend.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

//import io.github.jpaadmin.annotations.AdminResource;
//import io.github.jpaadmin.annotations.SortDir;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
// @AdminResource(
//     displayName = "Student",
//     pluralName = "Students",
//     group = "Student",
//     icon = "package"
// )


@Getter
@Setter
@Entity
@Table(name = "students")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = """
    UPDATE students
    SET deleted = true,
        version = version + 1
    WHERE id = ? AND version = ?
    """)
//@SQLRestriction("deleted = false")
@FilterDef(name = "deletedFilter", autoEnabled = true)
@Filter(name = "deletedFilter", condition = "deleted = false")
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String password;

    @Builder.Default
    private String role="USER";

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true) // default: fetch = FetchType.EAGER
    @JoinColumn(name = "address_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)       // default: fetch = FetchType.EAGER
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})   // default: fetch = FetchType.LAZY
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")

    )
    @Builder.Default
    private List<Course> courses = new ArrayList<>();  // owning side


}
