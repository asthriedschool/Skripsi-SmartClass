package com.smartclass.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogModel {
    private Long id_activity_log;
    private Long id_user;
    private Long id_room;
    private Long id_device;
    private Long id_schedule;
    private String activity_type;
    private String description;
    private String source;
    private LocalDateTime created_at;
}
