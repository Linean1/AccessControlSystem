package com.accesscontrol.dao;

import com.accesscontrol.entity.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {
}