package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER_TABLE")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;
}