package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Column(name = "FULL_NAME")
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "POSITION_ID")
    private Position position;

    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
}