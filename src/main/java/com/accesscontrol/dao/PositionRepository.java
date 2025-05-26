package com.accesscontrol.dao;

import com.accesscontrol.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}