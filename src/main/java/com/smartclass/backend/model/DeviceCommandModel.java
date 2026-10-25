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
public class DeviceCommandModel {
    private Long id_command;
    private Long id_device;
    private Long id_user;
    private String command;
    private String source;
    private String command_status;
    private String error_message;
    private LocalDateTime created_at;
    private LocalDateTime executed_at;
}
