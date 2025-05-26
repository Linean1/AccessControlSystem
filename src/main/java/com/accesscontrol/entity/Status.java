package com.accesscontrol.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "STATUS")
@Getter
@Setter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_seq")
    @SequenceGenerator(name = "status_seq", sequenceName = "status_seq", allocationSize = 1)
    @Column(name = "STATUS_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;
}