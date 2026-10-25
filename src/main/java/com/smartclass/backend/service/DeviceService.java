package com.smartclass.backend.service;

import com.smartclass.backend.entity.DeviceEntity;
import com.smartclass.backend.entity.enumsDeviceEntity.Status;
import com.smartclass.backend.repository.DeviceRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // GET semua device
    public List<DeviceEntity> getAllDevices() {
        return deviceRepository.findAll();
    }

    // GET device berdasarkan ID
    public DeviceEntity getDeviceById(Long id_device) {

        if (id_device == null) {
            throw new IllegalArgumentException(
                    "ID device tidak boleh null"
            );
        }

        return deviceRepository.findById(id_device)
                .orElseThrow(() -> new RuntimeException(
                        "Device dengan ID " + id_device + " tidak ditemukan"
                ));
    }

    // CREATE device
    public DeviceEntity createDevice(DeviceEntity device) {

        if (device == null) {
            throw new IllegalArgumentException(
                    "Data device tidak boleh null"
            );
        }

        validateDevice(device);

        return deviceRepository.save(device);
    }

    // UPDATE device
    public DeviceEntity updateDevice(
            Long id_device,
            DeviceEntity deviceData
    ) {

        if (id_device == null) {
            throw new IllegalArgumentException(
                    "ID device tidak boleh null"
            );
        }

        if (deviceData == null) {
            throw new IllegalArgumentException(
                    "Data device tidak boleh null"
            );
        }

        DeviceEntity device = deviceRepository.findById(id_device)
                .orElseThrow(() -> new RuntimeException(
                        "Device dengan ID " + id_device + " tidak ditemukan"
                ));

        validateDevice(deviceData);

        device.setId_room(deviceData.getId_room());
        device.setDevice_name(deviceData.getDevice_name());
        device.setDevice_code(deviceData.getDevice_code());
        device.setDevice_type(deviceData.getDevice_type());
        device.setStatus(deviceData.getStatus());
        device.setIs_enabled(deviceData.getIs_enabled());
        device.setLast_seen_at(deviceData.getLast_seen_at());

        return deviceRepository.save(device);
    }

    // DELETE device
    public void deleteDevice(Long id_device) {

        if (id_device == null) {
            throw new IllegalArgumentException(
                    "ID device tidak boleh null"
            );
        }

        if (!deviceRepository.existsById(id_device)) {
            throw new RuntimeException(
                    "Device dengan ID " + id_device + " tidak ditemukan"
            );
        }

        deviceRepository.deleteById(id_device);
    }

    // UPDATE status
    public DeviceEntity updateStatus(
            Long id_device,
            Status status
    ) {

        DeviceEntity device = getDeviceById(id_device);

        if (status == null) {
            throw new IllegalArgumentException(
                    "Status tidak boleh null"
            );
        }

        device.setStatus(status);

        return deviceRepository.save(device);
    }

    // UPDATE is enabled
    public DeviceEntity updateEnabled(
            Long id_device,
            Boolean isEnabled
    ) {

        DeviceEntity device = getDeviceById(id_device);

        if (isEnabled == null) {
            throw new IllegalArgumentException(
                    "Is enabled tidak boleh null"
            );
        }

        device.setIs_enabled(isEnabled);

        return deviceRepository.save(device);
    }

    // Validasi data device
    private void validateDevice(DeviceEntity device) {

        if (device.getId_room() == null) {
            throw new IllegalArgumentException(
                    "ID room wajib diisi"
            );
        }

        if (device.getDevice_name() == null ||
                device.getDevice_name().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Nama device wajib diisi"
            );
        }

        if (device.getDevice_code() == null ||
                device.getDevice_code().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Kode device wajib diisi"
            );
        }

        if (device.getDevice_type() == null) {
            throw new IllegalArgumentException(
                    "Tipe device wajib diisi"
            );
        }

        if (device.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Status wajib diisi"
            );
        }

        if (device.getIs_enabled() == null) {
            throw new IllegalArgumentException(
                    "Is enabled wajib diisi"
            );
        }
    }
}
