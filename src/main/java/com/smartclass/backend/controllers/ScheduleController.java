package com.smartclass.backend.controllers;

import com.smartclass.backend.dto.ScheduleRequest;
import com.smartclass.backend.entity.ScheduleEntity;
import com.smartclass.backend.service.ScheduleService;

import org.springframework.security.core.Authentication;
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
            @RequestBody ScheduleRequest schedule,
            Authentication authentication
    ) {

        /*
         * =========================================================
         * PSEUDOCODE CREATE SCHEDULE
         * =========================================================
         *
         * 1. User login menggunakan email + password.
         *
         * 2. Setelah login berhasil, backend membuat JWT.
         *
         * 3. Flutter mengirim JWT pada Authorization Header:
         *
         *      Authorization: Bearer <accessToken>
         *
         * 4. JwtAuthFilter memvalidasi JWT.
         *
         * 5. Jika JWT valid, Authentication berisi
         *    identitas user yang sedang login.
         *
         * 6. authentication.getName()
         *    mengambil email user.
         *
         * 7. ScheduleService mencari user berdasarkan email.
         *
         * 8. Backend mengambil id_user dari user tersebut.
         *
         * 9. Backend otomatis mengisi:
         *
         *      schedule.created_by = id_user
         *
         * 10. Data schedule disimpan ke database.
         *
         * Jadi client TIDAK perlu mengirim created_by.
         *
         * =========================================================
         */

        String userEmail = authentication.getName();

        return scheduleService.createSchedule(
                schedule,
                userEmail
        );
    }

    // UPDATE schedule
    @PutMapping("/{id_schedule}")
    public ScheduleEntity updateSchedule(
            @PathVariable Long id_schedule,
            @RequestBody ScheduleEntity schedule
    ) {
        return scheduleService.updateSchedule(
                id_schedule,
                schedule
        );
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