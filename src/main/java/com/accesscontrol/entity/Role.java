package com.accesscontrol.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "ROLE")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    @Column(name = "ROLE_ID")
    private Long id;

    @Column(name = "ROLE_NAME")
    private String roleName;
}