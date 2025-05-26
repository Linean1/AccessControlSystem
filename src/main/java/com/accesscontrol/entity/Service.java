package com.accesscontrol.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "SERVICE_RESOURCE")
@Getter
@Setter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_seq")
    @SequenceGenerator(name = "service_seq", sequenceName = "service_seq", allocationSize = 1)
    @Column(name = "SERVICE_ID")
    private Long id;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "TYPE")
    private String type;
}