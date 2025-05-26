package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACCESS_REQUEST")
@Getter
@Setter
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_request_seq")
    @SequenceGenerator(name = "access_request_seq", sequenceName = "access_request_seq", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "SERVICE_ID")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @Column(name = "REQUEST_DATE")
    private LocalDateTime requestDate;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestHistory> requestHistories = new ArrayList<>();
}