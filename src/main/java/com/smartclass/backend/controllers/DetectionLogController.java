package com.smartclass.backend.controllers;

import com.smartclass.backend.entity.DetectionLogEntity;
import com.smartclass.backend.entity.enumsDetectionLogEntity.ActionTaken;
import com.smartclass.backend.service.DetectionLogService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detection-logs")
public class DetectionLogController {

    private final DetectionLogService detectionLogService;

    public DetectionLogController(DetectionLogService detectionLogService) {
        this.detectionLogService = detectionLogService;
    }

    // GET semua detection log
    @GetMapping
    public List<DetectionLogEntity> getAllDetectionLogs() {
        return detectionLogService.getAllDetectionLogs();
    }

    // GET satu detection log berdasarkan ID
    @GetMapping("/{id_detection_log}")
    public DetectionLogEntity getDetectionLogById(
            @PathVariable Long id_detection_log
    ) {
        return detectionLogService.getDetectionLogById(id_detection_log);
    }

    // CREATE detection log
    @PostMapping
    public DetectionLogEntity createDetectionLog(
            @RequestBody DetectionLogEntity detectionLog
    ) {
        return detectionLogService.createDetectionLog(detectionLog);
    }

    // UPDATE detection log
    @PutMapping("/{id_detection_log}")
    public DetectionLogEntity updateDetectionLog(
            @PathVariable Long id_detection_log,
            @RequestBody DetectionLogEntity detectionLog
    ) {
        return detectionLogService.updateDetectionLog(id_detection_log, detectionLog);
    }

    // DELETE detection log
    @DeleteMapping("/{id_detection_log}")
    public String deleteDetectionLog(
            @PathVariable Long id_detection_log
    ) {
        detectionLogService.deleteDetectionLog(id_detection_log);

        return "Detection log berhasil dihapus";
    }

    // UPDATE action taken
    @PatchMapping("/{id_detection_log}/action")
    public DetectionLogEntity updateActionTaken(
            @PathVariable Long id_detection_log,
            @RequestParam ActionTaken actionTaken
    ) {
        return detectionLogService.updateActionTaken(
                id_detection_log,
                actionTaken
        );
    }
}
