package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.RoomEntity;
import com.smartclass.backend.entity.enumsRoomEntity.OperationMode;
import com.smartclass.backend.entity.enumsRoomEntity.SystemStatus;
import com.smartclass.backend.service.RoomService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET semua room
    @GetMapping
    public List<RoomEntity> getAllRooms() {
        return roomService.getAllRooms();
    }

    // GET satu room berdasarkan ID
    @GetMapping("/{id_room}")
    public RoomEntity getRoomById(
            @PathVariable Long id_room
    ) {
        return roomService.getRoomById(id_room);
    }

    // CREATE room
    @PostMapping
    public RoomEntity createRoom(
            @RequestBody RoomEntity room
    ) {
        return roomService.createRoom(room);
    }

    // UPDATE room
    @PutMapping("/{id_room}")
    public RoomEntity updateRoom(
            @PathVariable Long id_room,
            @RequestBody RoomEntity room
    ) {
        return roomService.updateRoom(id_room, room);
    }

    // DELETE room
    @DeleteMapping("/{id_room}")
    public String deleteRoom(
            @PathVariable Long id_room
    ) {
        roomService.deleteRoom(id_room);

        return "Room berhasil dihapus";
    }

    // UPDATE system status
    @PatchMapping("/{id_room}/status")
    public RoomEntity updateSystemStatus(
            @PathVariable Long id_room,
            @RequestParam SystemStatus systemStatus
    ) {
        return roomService.updateSystemStatus(
                id_room,
                systemStatus
        );
    }

    // UPDATE operation mode
    @PatchMapping("/{id_room}/mode")
    public RoomEntity updateOperationMode(
            @PathVariable Long id_room,
            @RequestParam OperationMode operationMode
    ) {
        return roomService.updateOperationMode(
                id_room,
                operationMode
        );
    }

    // UPDATE auto off delay
    @PatchMapping("/{id_room}/auto-off-delay")
    public RoomEntity updateAutoOffDelay(
            @PathVariable Long id_room,
            @RequestParam Integer minutes
    ) {
        return roomService.updateAutoOffDelay(
                id_room,
                minutes
        );
    }
}