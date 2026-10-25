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
public class RoomModel {
    private Long id_room;
    private String room_name;
    private String building;
    private String floor;
    private String picture;
    private String system_status;
    private String operation_mode;
    private Integer auto_off_delay_minutes;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
