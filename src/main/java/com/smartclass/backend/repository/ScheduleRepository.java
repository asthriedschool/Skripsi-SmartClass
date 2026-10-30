package com.smartclass.backend.repository;

import com.smartclass.backend.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalTime;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("""
            SELECT s
            FROM ScheduleEntity s
            WHERE s.id_room = :roomId
              AND s.is_cancelled = false
              AND (:excludedScheduleId IS NULL
                   OR s.id_schedule <> :excludedScheduleId)
              AND s.start_time < :endTime
              AND s.end_time > :startTime
            """)
    List<ScheduleEntity> findActiveOverlappingSchedules(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludedScheduleId") Long excludedScheduleId
    );
}
