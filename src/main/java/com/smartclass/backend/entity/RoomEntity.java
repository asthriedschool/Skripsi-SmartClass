package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsRoomEntity.OperationMode;
import com.smartclass.backend.entity.enumsRoomEntity.SystemStatus;

import com.smartclass.backend.entity.enumsRoomEntity.OperationMode;
import com.smartclass.backend.entity.enumsRoomEntity.SystemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_rooms")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_room;

    @Column(name = "room_name", nullable = false, length = 100)
    private String room_name;

    @Column(name = "building", nullable = false, length = 100)
    private String building;

    @Column(name = "floor", nullable = false, length = 20)
    private String floor;

    @Column(name = "picture", length = 255)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_status", nullable = false)
    private SystemStatus system_status;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_mode", nullable = false)
    private OperationMode operation_mode;

    @Column(name = "auto_off_delay_minutes", nullable = false)
    private Integer auto_off_delay_minutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

}
