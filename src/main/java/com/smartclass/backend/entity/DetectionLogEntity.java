package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsDetectionLogEntity.ActionTaken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_detection_logs")
public class DetectionLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_detection_log;

    @Column(name = "id_room", nullable = false)
    private Long id_room;

    @Column(name = "motion_detected", nullable = false)
    private Boolean motion_detected;

    @Column(name = "human_detected", nullable = false)
    private Boolean human_detected;

    @Column(name = "confidence", precision = 5, scale = 2)
    private BigDecimal confidence;

    @Column(name = "image_path", length = 255)
    private String image_path;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken", nullable = false)
    private ActionTaken action_taken;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detected_at;

    @PrePersist
    protected void onCreate() {
        detected_at = LocalDateTime.now();
    }

}
