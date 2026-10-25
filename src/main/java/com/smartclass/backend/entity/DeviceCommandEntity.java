package com.smartclass.backend.entity;

import com.smartclass.backend.entity.enumsDeviceCommandEntity.Command;
import com.smartclass.backend.entity.enumsDeviceCommandEntity.CommandStatus;
import com.smartclass.backend.entity.enumsDeviceCommandEntity.Source;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_device_commands")
public class DeviceCommandEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_command;

    @Column(name = "id_device", nullable = false)
    private Long id_device;

    @Column(name = "id_user")
    private Long id_user;

    @Enumerated(EnumType.STRING)
    @Column(name = "command", nullable = false)
    private Command command;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_status", nullable = false)
    private CommandStatus command_status;

    @Column(name = "error_message", length = 255)
    private String error_message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "executed_at")
    private LocalDateTime executed_at;

}
