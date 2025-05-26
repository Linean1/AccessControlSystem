package com.accesscontrol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUEST_HISTORY")
@Getter
@Setter
public class RequestHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_history_seq")
    @SequenceGenerator(name = "request_history_seq", sequenceName = "request_history_seq", allocationSize = 1)
    @Column(name = "HISTORY_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "REQUEST_ID")
    private AccessRequest request;

    @Column(name = "CHANGE_DATE")
    private LocalDateTime changeDate;

    @ManyToOne
    @JoinColumn(name = "STATUS_ID")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "AUTHOR")
    private User author;
}