package com.bookmarkhub.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bootstrap_probe")
public class BootstrapProbe {

    // Temporary probe entity for Task 3 only.
    // It forces Hibernate schema validation to fail on a missing table so the
    // scaffold task can stop at a truthful "missing database objects" checkpoint
    // before real domain entities and Flyway migrations arrive in later tasks.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
}
