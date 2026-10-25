package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsDeviceEntity.DeviceType;
import com.smartclass.backend.entity.enumsDeviceEntity.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_devices")
public class DeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_device;

    @Column(name = "id_room", nullable = false)
    private Long id_room;

    @Column(name = "device_name", nullable = false, length = 100)
    private String device_name;

    @Column(name = "device_code", nullable = false, length = 100)
    private String device_code;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType device_type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "is_enabled", nullable = false)
    private Boolean is_enabled;

    @Column(name = "last_seen_at")
    private LocalDateTime last_seen_at;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


}
