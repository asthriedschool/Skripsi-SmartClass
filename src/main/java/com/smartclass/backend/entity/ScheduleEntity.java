package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsScheduleEntity.DayOfWeek;
import com.smartclass.backend.entity.enumsScheduleEntity.ScheduleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "tb_schedules")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_schedule;

    @Column(name = "id_room", nullable = false)
    private Long id_room;

    @Column(name = "created_by")
    private Long created_by;

    @Column(name = "activity_name", nullable = false, length = 150)
    private String activity_name;

    @Column(name = "lecturer_name", length = 100)
    private String lecturer_name;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType schedule_type;

    @Column(name = "schedule_date")
    private LocalDate schedule_date;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek day_of_week;

    @Column(name = "start_time", nullable = false)
    private LocalTime start_time;

    @Column(name = "end_time", nullable = false)
    private LocalTime end_time;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean is_cancelled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        created_at = now;
        updated_at = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

}
