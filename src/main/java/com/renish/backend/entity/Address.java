package com.renish.backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor
@Entity
@Table(name = "addresses")
@Builder 
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private String state;

    private String country;

    @OneToOne(mappedBy = "address")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Student student;
}
