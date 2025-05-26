package com.accesscontrol.dao;

import com.accesscontrol.entity.AccessRequest;
import com.accesscontrol.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByUser(User user);
}