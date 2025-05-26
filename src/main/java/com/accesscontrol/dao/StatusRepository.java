package com.accesscontrol.dao;

import com.accesscontrol.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findByName(String name);
}