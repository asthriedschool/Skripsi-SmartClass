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
public class DeviceModel {
    private Long id_device;
    private Long id_room;
    private String device_name;
    private String device_code;
    private String device_type;
    private String status;
    private Boolean is_enabled;
    private LocalDateTime last_seen_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
