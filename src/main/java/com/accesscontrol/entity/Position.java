package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "POSITION")
@Getter
@Setter
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_seq")
    @SequenceGenerator(name = "position_seq", sequenceName = "position_seq", allocationSize = 1)
    @Column(name = "POSITION_ID")
    private Long id;

    @Column(name = "POSITION_NAME")
    private String positionName;
}