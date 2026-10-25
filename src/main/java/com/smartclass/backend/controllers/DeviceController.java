package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.DeviceEntity;
import com.smartclass.backend.entity.enumsDeviceEntity.Status;
import com.smartclass.backend.service.DeviceService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // GET semua device
    @GetMapping
    public List<DeviceEntity> getAllDevices() {
        return deviceService.getAllDevices();
    }

    // GET satu device berdasarkan ID
    @GetMapping("/{id_device}")
    public DeviceEntity getDeviceById(
            @PathVariable Long id_device
    ) {
        return deviceService.getDeviceById(id_device);
    }

    // CREATE device
    @PostMapping
    public DeviceEntity createDevice(
            @RequestBody DeviceEntity device
    ) {
        return deviceService.createDevice(device);
    }

    // UPDATE device
    @PutMapping("/{id_device}")
    public DeviceEntity updateDevice(
            @PathVariable Long id_device,
            @RequestBody DeviceEntity device
    ) {
        return deviceService.updateDevice(id_device, device);
    }

    // DELETE device
    @DeleteMapping("/{id_device}")
    public String deleteDevice(
            @PathVariable Long id_device
    ) {
        deviceService.deleteDevice(id_device);

        return "Device berhasil dihapus";
    }

    // UPDATE status
    @PatchMapping("/{id_device}/status")
    public DeviceEntity updateStatus(
            @PathVariable Long id_device,
            @RequestParam Status status
    ) {
        return deviceService.updateStatus(
                id_device,
                status
        );
    }

    // UPDATE is enabled
    @PatchMapping("/{id_device}/enabled")
    public DeviceEntity updateEnabled(
            @PathVariable Long id_device,
            @RequestParam Boolean isEnabled
    ) {
        return deviceService.updateEnabled(
                id_device,
                isEnabled
        );
    }
}
