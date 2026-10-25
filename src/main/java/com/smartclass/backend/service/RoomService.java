package com.smartclass.backend.service;

import com.smartclass.backend.entity.RoomEntity;
import com.smartclass.backend.entity.enumsRoomEntity.OperationMode;
import com.smartclass.backend.entity.enumsRoomEntity.SystemStatus;
import com.smartclass.backend.repository.RoomRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // GET semua room
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    // GET room berdasarkan ID
    public RoomEntity getRoomById(Long id_room) {

        if (id_room == null) {
            throw new IllegalArgumentException(
                    "ID room tidak boleh null"
            );
        }

        return roomRepository.findById(id_room)
                .orElseThrow(() -> new RuntimeException(
                        "Room dengan ID " + id_room + " tidak ditemukan"
                ));
    }

    // CREATE room
    public RoomEntity createRoom(RoomEntity room) {

        if (room == null) {
            throw new IllegalArgumentException(
                    "Data room tidak boleh null"
            );
        }

        validateRoom(room);

        return roomRepository.save(room);
    }

    // UPDATE room
    public RoomEntity updateRoom(
            Long id_room,
            RoomEntity roomData
    ) {

        if (id_room == null) {
            throw new IllegalArgumentException(
                    "ID room tidak boleh null"
            );
        }

        if (roomData == null) {
            throw new IllegalArgumentException(
                    "Data room tidak boleh null"
            );
        }

        RoomEntity room = roomRepository.findById(id_room)
                .orElseThrow(() -> new RuntimeException(
                        "Room dengan ID " + id_room + " tidak ditemukan"
                ));

        validateRoom(roomData);

        room.setRoom_name(roomData.getRoom_name());
        room.setBuilding(roomData.getBuilding());
        room.setFloor(roomData.getFloor());
        room.setPicture(roomData.getPicture());
        room.setSystem_status(roomData.getSystem_status());
        room.setOperation_mode(roomData.getOperation_mode());
        room.setAuto_off_delay_minutes(
                roomData.getAuto_off_delay_minutes()
        );

        return roomRepository.save(room);
    }

    // DELETE room
    public void deleteRoom(Long id_room) {

        if (id_room == null) {
            throw new IllegalArgumentException(
                    "ID room tidak boleh null"
            );
        }

        if (!roomRepository.existsById(id_room)) {
            throw new RuntimeException(
                    "Room dengan ID " + id_room + " tidak ditemukan"
            );
        }

        roomRepository.deleteById(id_room);
    }

    // UPDATE system status
    public RoomEntity updateSystemStatus(
            Long id_room,
            SystemStatus systemStatus
    ) {

        RoomEntity room = getRoomById(id_room);

        if (systemStatus == null) {
            throw new IllegalArgumentException(
                    "System status tidak boleh null"
            );
        }

        room.setSystem_status(systemStatus);

        return roomRepository.save(room);
    }

    // UPDATE operation mode
    public RoomEntity updateOperationMode(
            Long id_room,
            OperationMode operationMode
    ) {

        RoomEntity room = getRoomById(id_room);

        if (operationMode == null) {
            throw new IllegalArgumentException(
                    "Operation mode tidak boleh null"
            );
        }

        room.setOperation_mode(operationMode);

        return roomRepository.save(room);
    }

    // UPDATE auto off delay
    public RoomEntity updateAutoOffDelay(
            Long id_room,
            Integer autoOffDelayMinutes
    ) {

        RoomEntity room = getRoomById(id_room);

        if (autoOffDelayMinutes == null) {
            throw new IllegalArgumentException(
                    "Auto off delay tidak boleh null"
            );
        }

        if (autoOffDelayMinutes < 0) {
            throw new IllegalArgumentException(
                    "Auto off delay tidak boleh kurang dari 0"
            );
        }

        room.setAuto_off_delay_minutes(autoOffDelayMinutes);

        return roomRepository.save(room);
    }

    // Validasi data room
    private void validateRoom(RoomEntity room) {

        if (room.getRoom_name() == null ||
                room.getRoom_name().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Nama room wajib diisi"
            );
        }

        if (room.getBuilding() == null ||
                room.getBuilding().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Building wajib diisi"
            );
        }

        if (room.getFloor() == null ||
                room.getFloor().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Floor wajib diisi"
            );
        }

        if (room.getSystem_status() == null) {
            throw new IllegalArgumentException(
                    "System status wajib diisi"
            );
        }

        if (room.getOperation_mode() == null) {
            throw new IllegalArgumentException(
                    "Operation mode wajib diisi"
            );
        }

        if (room.getAuto_off_delay_minutes() == null) {
            throw new IllegalArgumentException(
                    "Auto off delay wajib diisi"
            );
        }

        if (room.getAuto_off_delay_minutes() < 0) {
            throw new IllegalArgumentException(
                    "Auto off delay tidak boleh kurang dari 0"
            );
        }
    }
}