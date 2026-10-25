package com.smartclass.backend.repository;

import com.smartclass.backend.entity.DetectionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectionLogRepository extends JpaRepository<DetectionLogEntity, Long> {
}
