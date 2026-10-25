package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsActivityLogEntity.Source;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.ZoneId;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_activity_logs")
public class ActivityLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_activity_log;

    @Column(name = "id_user")
    private Long id_user;

    @Column(name = "id_room")
    private Long id_room;

    @Column(name = "id_device")
    private Long id_device;

    @Column(name = "id_schedule")
    private Long id_schedule;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activity_type;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now(
                ZoneId.of("Asia/Makassar")
        );
    }

}
