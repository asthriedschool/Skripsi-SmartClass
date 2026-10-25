package com.smartclass.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetectionLogModel {
    private Long id_detection_log;
    private Long id_room;
    private Boolean motion_detected;
    private Boolean human_detected;
    private BigDecimal confidence;
    private String image_path;
    private String action_taken;
    private LocalDateTime detected_at;
}
