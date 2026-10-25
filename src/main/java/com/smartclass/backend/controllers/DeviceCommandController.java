package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.DeviceCommandEntity;
import com.smartclass.backend.entity.enumsDeviceCommandEntity.CommandStatus;
import com.smartclass.backend.service.DeviceCommandService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-commands")
public class DeviceCommandController {

    private final DeviceCommandService deviceCommandService;

    public DeviceCommandController(DeviceCommandService deviceCommandService) {
        this.deviceCommandService = deviceCommandService;
    }

    // GET semua device command
    @GetMapping
    public List<DeviceCommandEntity> getAllDeviceCommands() {
        return deviceCommandService.getAllDeviceCommands();
    }

    // GET satu device command berdasarkan ID
    @GetMapping("/{id_command}")
    public DeviceCommandEntity getDeviceCommandById(
            @PathVariable Long id_command
    ) {
        return deviceCommandService.getDeviceCommandById(id_command);
    }

    // CREATE device command
    @PostMapping
    public DeviceCommandEntity createDeviceCommand(
            @RequestBody DeviceCommandEntity command
    ) {
        return deviceCommandService.createDeviceCommand(command);
    }

    // UPDATE device command
    @PutMapping("/{id_command}")
    public DeviceCommandEntity updateDeviceCommand(
            @PathVariable Long id_command,
            @RequestBody DeviceCommandEntity command
    ) {
        return deviceCommandService.updateDeviceCommand(id_command, command);
    }

    // DELETE device command
    @DeleteMapping("/{id_command}")
    public String deleteDeviceCommand(
            @PathVariable Long id_command
    ) {
        deviceCommandService.deleteDeviceCommand(id_command);

        return "Device command berhasil dihapus";
    }

    // UPDATE command status
    @PatchMapping("/{id_command}/status")
    public DeviceCommandEntity updateCommandStatus(
            @PathVariable Long id_command,
            @RequestParam CommandStatus commandStatus
    ) {
        return deviceCommandService.updateCommandStatus(
                id_command,
                commandStatus
        );
    }
}
