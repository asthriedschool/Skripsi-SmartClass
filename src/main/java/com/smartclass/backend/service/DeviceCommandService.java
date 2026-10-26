package com.smartclass.backend.service;

import com.smartclass.backend.entity.DeviceCommandEntity;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.entity.enumsDeviceCommandEntity.CommandStatus;
import com.smartclass.backend.repository.DeviceCommandRepository;
import com.smartclass.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceCommandService {

    private final DeviceCommandRepository deviceCommandRepository;
    private final UserRepository userRepository;

    public DeviceCommandService(
            DeviceCommandRepository deviceCommandRepository,
            UserRepository userRepository
    ) {
        this.deviceCommandRepository = deviceCommandRepository;
        this.userRepository = userRepository;
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
                        "Device command dengan ID " + id_command
                                + " tidak ditemukan"
                ));
    }

    // CREATE device command
    public DeviceCommandEntity createDeviceCommand(
            DeviceCommandEntity command,
            String userEmail
    ) {

        /*
         * =========================================================
         * PSEUDOCODE CREATE DEVICE COMMAND
         * =========================================================
         *
         * 1. User login menggunakan email + password.
         *
         * 2. Backend membuat Access Token JWT.
         *
         * 3. Client mengirim JWT:
         *
         *      Authorization: Bearer <accessToken>
         *
         * 4. JwtAuthFilter memvalidasi JWT.
         *
         * 5. Authentication berisi user yang sedang login.
         *
         * 6. Controller mengambil email dari:
         *
         *      authentication.getName()
         *
         * 7. Service mencari user berdasarkan email.
         *
         * 8. Backend mengambil id_user.
         *
         * 9. Backend otomatis mengisi:
         *
         *      command.id_user = user.id_user
         *
         * 10. Backend tidak mempercayai id_user
         *     yang dikirim oleh client.
         *
         * 11. Validasi command.
         *
         * 12. Simpan command ke database.
         *
         * =========================================================
         */

        if (command == null) {
            throw new IllegalArgumentException(
                    "Data device command tidak boleh null"
            );
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "User yang login tidak ditemukan"
            );
        }

        UserEntity user = userRepository.findByEmail(
                userEmail.trim()
        ).orElseThrow(() -> new RuntimeException(
                "User dengan email " + userEmail
                        + " tidak ditemukan"
        ));

        /*
         * id_user diambil dari user yang sedang login.
         * Nilai id_user dari request client tidak dipercaya.
         */
        command.setId_user(user.getId_user());

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

        DeviceCommandEntity command =
                deviceCommandRepository.findById(id_command)
                        .orElseThrow(() -> new RuntimeException(
                                "Device command dengan ID "
                                        + id_command
                                        + " tidak ditemukan"
                        ));

        validateDeviceCommand(commandData);

        command.setId_device(
                commandData.getId_device()
        );

        /*
         * =========================================================
         * PENTING
         * =========================================================
         *
         * id_user TIDAK diubah ketika UPDATE.
         *
         * User yang pertama kali membuat command
         * tetap tercatat sebagai user yang membuatnya.
         *
         * Jangan gunakan:
         *
         * command.setId_user(commandData.getId_user());
         *
         * karena client tidak boleh mengganti identitas
         * user pembuat command.
         * =========================================================
         */

        command.setCommand(
                commandData.getCommand()
        );

        command.setSource(
                commandData.getSource()
        );

        command.setCommand_status(
                commandData.getCommand_status()
        );

        command.setError_message(
                commandData.getError_message()
        );

        command.setExecuted_at(
                commandData.getExecuted_at()
        );

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
                    "Device command dengan ID " + id_command
                            + " tidak ditemukan"
            );
        }

        deviceCommandRepository.deleteById(id_command);
    }

    // UPDATE command status
    public DeviceCommandEntity updateCommandStatus(
            Long id_command,
            CommandStatus commandStatus
    ) {

        DeviceCommandEntity command =
                getDeviceCommandById(id_command);

        if (commandStatus == null) {
            throw new IllegalArgumentException(
                    "Command status tidak boleh null"
            );
        }

        command.setCommand_status(commandStatus);

        return deviceCommandRepository.save(command);
    }

    // Validasi data device command
    private void validateDeviceCommand(
            DeviceCommandEntity command
    ) {

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