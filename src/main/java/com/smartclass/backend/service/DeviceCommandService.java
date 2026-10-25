package com.smartclass.backend.service;

import com.smartclass.backend.entity.DeviceCommandEntity;
import com.smartclass.backend.entity.enumsDeviceCommandEntity.CommandStatus;
import com.smartclass.backend.repository.DeviceCommandRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceCommandService {

    private final DeviceCommandRepository deviceCommandRepository;

    public DeviceCommandService(DeviceCommandRepository deviceCommandRepository) {
        this.deviceCommandRepository = deviceCommandRepository;
    }

    // GET semua device command
    public List<DeviceCommandEntity> getAllDeviceCommands() {
        return deviceCommandRepository.findAll();
    }

    // GET device command berdasarkan ID
    public DeviceCommandEntity getDeviceCommandById(Long id_command) {

        if (id_command == null) {
            throw new IllegalArgumentException(
                    "ID command tidak boleh null"
            );
        }

        return deviceCommandRepository.findById(id_command)
                .orElseThrow(() -> new RuntimeException(
                        "Device command dengan ID " + id_command + " tidak ditemukan"
                ));
    }

    // CREATE device command
    public DeviceCommandEntity createDeviceCommand(DeviceCommandEntity command) {

        if (command == null) {
            throw new IllegalArgumentException(
                    "Data device command tidak boleh null"
            );
        }

        validateDeviceCommand(command);

        return deviceCommandRepository.save(command);
    }

    // UPDATE device command
    public DeviceCommandEntity updateDeviceCommand(
            Long id_command,
            DeviceCommandEntity commandData
    ) {

        if (id_command == null) {
            throw new IllegalArgumentException(
                    "ID command tidak boleh null"
            );
        }

        if (commandData == null) {
            throw new IllegalArgumentException(
                    "Data device command tidak boleh null"
            );
        }

        DeviceCommandEntity command = deviceCommandRepository.findById(id_command)
                .orElseThrow(() -> new RuntimeException(
                        "Device command dengan ID " + id_command + " tidak ditemukan"
                ));

        validateDeviceCommand(commandData);

        command.setId_device(commandData.getId_device());
        command.setId_user(commandData.getId_user());
        command.setCommand(commandData.getCommand());
        command.setSource(commandData.getSource());
        command.setCommand_status(commandData.getCommand_status());
        command.setError_message(commandData.getError_message());
        command.setExecuted_at(commandData.getExecuted_at());

        return deviceCommandRepository.save(command);
    }

    // DELETE device command
    public void deleteDeviceCommand(Long id_command) {

        if (id_command == null) {
            throw new IllegalArgumentException(
                    "ID command tidak boleh null"
            );
        }

        if (!deviceCommandRepository.existsById(id_command)) {
            throw new RuntimeException(
                    "Device command dengan ID " + id_command + " tidak ditemukan"
            );
        }

        deviceCommandRepository.deleteById(id_command);
    }

    // UPDATE command status
    public DeviceCommandEntity updateCommandStatus(
            Long id_command,
            CommandStatus commandStatus
    ) {

        DeviceCommandEntity command = getDeviceCommandById(id_command);

        if (commandStatus == null) {
            throw new IllegalArgumentException(
                    "Command status tidak boleh null"
            );
        }

        command.setCommand_status(commandStatus);

        return deviceCommandRepository.save(command);
    }

    // Validasi data device command
    private void validateDeviceCommand(DeviceCommandEntity command) {

        if (command.getId_device() == null) {
            throw new IllegalArgumentException(
                    "ID device wajib diisi"
            );
        }

        if (command.getCommand() == null) {
            throw new IllegalArgumentException(
                    "Command wajib diisi"
            );
        }

        if (command.getSource() == null) {
            throw new IllegalArgumentException(
                    "Source wajib diisi"
            );
        }

        if (command.getCommand_status() == null) {
            throw new IllegalArgumentException(
                    "Command status wajib diisi"
            );
        }
    }
}
