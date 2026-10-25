package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.ActivityLogEntity;
import com.smartclass.backend.entity.enumsActivityLogEntity.Source;
import com.smartclass.backend.service.ActivityLogService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // GET semua activity log
    @GetMapping
    public List<ActivityLogEntity> getAllActivityLogs() {
        return activityLogService.getAllActivityLogs();
    }

    // GET satu activity log berdasarkan ID
    @GetMapping("/{id_activity_log}")
    public ActivityLogEntity getActivityLogById(
            @PathVariable Long id_activity_log
    ) {
        return activityLogService.getActivityLogById(id_activity_log);
    }

    // CREATE activity log
    @PostMapping
    public ActivityLogEntity createActivityLog(
            @RequestBody ActivityLogEntity activityLog
    ) {
        return activityLogService.createActivityLog(activityLog);
    }

    // UPDATE activity log
    @PutMapping("/{id_activity_log}")
    public ActivityLogEntity updateActivityLog(
            @PathVariable Long id_activity_log,
            @RequestBody ActivityLogEntity activityLog
    ) {
        return activityLogService.updateActivityLog(id_activity_log, activityLog);
    }

    // DELETE activity log
    @DeleteMapping("/{id_activity_log}")
    public String deleteActivityLog(
            @PathVariable Long id_activity_log
    ) {
        activityLogService.deleteActivityLog(id_activity_log);

        return "Activity log berhasil dihapus";
    }

    // UPDATE source
    @PatchMapping("/{id_activity_log}/source")
    public ActivityLogEntity updateSource(
            @PathVariable Long id_activity_log,
            @RequestParam Source source
    ) {
        return activityLogService.updateSource(
                id_activity_log,
                source
        );
    }
}
