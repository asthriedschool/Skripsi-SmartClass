package com.smartclass.backend.repository;

import com.smartclass.backend.entity.DeviceCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommandEntity, Long> {
}
