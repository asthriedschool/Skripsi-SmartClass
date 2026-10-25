package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.ScheduleEntity;
import com.smartclass.backend.service.ScheduleService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // GET semua schedule
    @GetMapping
    public List<ScheduleEntity> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    // GET satu schedule berdasarkan ID
    @GetMapping("/{id_schedule}")
    public ScheduleEntity getScheduleById(
            @PathVariable Long id_schedule
    ) {
        return scheduleService.getScheduleById(id_schedule);
    }

    // CREATE schedule
    @PostMapping
    public ScheduleEntity createSchedule(
            @RequestBody ScheduleEntity schedule
    ) {
        return scheduleService.createSchedule(schedule);
    }

    // UPDATE schedule
    @PutMapping("/{id_schedule}")
    public ScheduleEntity updateSchedule(
            @PathVariable Long id_schedule,
            @RequestBody ScheduleEntity schedule
    ) {
        return scheduleService.updateSchedule(id_schedule, schedule);
    }

    // DELETE schedule
    @DeleteMapping("/{id_schedule}")
    public String deleteSchedule(
            @PathVariable Long id_schedule
    ) {
        scheduleService.deleteSchedule(id_schedule);

        return "Schedule berhasil dihapus";
    }

    // UPDATE is cancelled
    @PatchMapping("/{id_schedule}/cancelled")
    public ScheduleEntity updateCancelled(
            @PathVariable Long id_schedule,
            @RequestParam Boolean isCancelled
    ) {
        return scheduleService.updateCancelled(
                id_schedule,
                isCancelled
        );
    }
}
