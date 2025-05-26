package com.accesscontrol.dao;

import com.accesscontrol.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}