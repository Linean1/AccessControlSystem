package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DEPARTMENT")
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "department_seq")
    @SequenceGenerator(name = "department_seq", sequenceName = "department_seq", allocationSize = 1)
    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
}