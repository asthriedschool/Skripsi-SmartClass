package com.smartclass.backend.service;

import com.smartclass.backend.entity.DetectionLogEntity;
import com.smartclass.backend.entity.enumsDetectionLogEntity.ActionTaken;
import com.smartclass.backend.repository.DetectionLogRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectionLogService {

    private final DetectionLogRepository detectionLogRepository;

    public DetectionLogService(DetectionLogRepository detectionLogRepository) {
        this.detectionLogRepository = detectionLogRepository;
    }

    // GET semua detection log
    public List<DetectionLogEntity> getAllDetectionLogs() {
        return detectionLogRepository.findAll();
    }

    // GET detection log berdasarkan ID
    public DetectionLogEntity getDetectionLogById(Long id_detection_log) {

        if (id_detection_log == null) {
            throw new IllegalArgumentException(
                    "ID detection log tidak boleh null"
            );
        }

        return detectionLogRepository.findById(id_detection_log)
                .orElseThrow(() -> new RuntimeException(
                        "Detection log dengan ID " + id_detection_log + " tidak ditemukan"
                ));
    }

    // CREATE detection log
    public DetectionLogEntity createDetectionLog(DetectionLogEntity detectionLog) {

        if (detectionLog == null) {
            throw new IllegalArgumentException(
                    "Data detection log tidak boleh null"
            );
        }

        validateDetectionLog(detectionLog);

        return detectionLogRepository.save(detectionLog);
    }

    // UPDATE detection log
    public DetectionLogEntity updateDetectionLog(
            Long id_detection_log,
            DetectionLogEntity detectionLogData
    ) {

        if (id_detection_log == null) {
            throw new IllegalArgumentException(
                    "ID detection log tidak boleh null"
            );
        }

        if (detectionLogData == null) {
            throw new IllegalArgumentException(
                    "Data detection log tidak boleh null"
            );
        }

        DetectionLogEntity detectionLog = detectionLogRepository.findById(id_detection_log)
                .orElseThrow(() -> new RuntimeException(
                        "Detection log dengan ID " + id_detection_log + " tidak ditemukan"
                ));

        validateDetectionLog(detectionLogData);

        detectionLog.setId_room(detectionLogData.getId_room());
        detectionLog.setMotion_detected(detectionLogData.getMotion_detected());
        detectionLog.setHuman_detected(detectionLogData.getHuman_detected());
        detectionLog.setConfidence(detectionLogData.getConfidence());
        detectionLog.setImage_path(detectionLogData.getImage_path());
        detectionLog.setAction_taken(detectionLogData.getAction_taken());

        return detectionLogRepository.save(detectionLog);
    }

    // DELETE detection log
    public void deleteDetectionLog(Long id_detection_log) {

        if (id_detection_log == null) {
            throw new IllegalArgumentException(
                    "ID detection log tidak boleh null"
            );
        }

        if (!detectionLogRepository.existsById(id_detection_log)) {
            throw new RuntimeException(
                    "Detection log dengan ID " + id_detection_log + " tidak ditemukan"
            );
        }

        detectionLogRepository.deleteById(id_detection_log);
    }

    // UPDATE action taken
    public DetectionLogEntity updateActionTaken(
            Long id_detection_log,
            ActionTaken actionTaken
    ) {

        DetectionLogEntity detectionLog = getDetectionLogById(id_detection_log);

        if (actionTaken == null) {
            throw new IllegalArgumentException(
                    "Action taken tidak boleh null"
            );
        }

        detectionLog.setAction_taken(actionTaken);

        return detectionLogRepository.save(detectionLog);
    }

    // Validasi data detection log
    private void validateDetectionLog(DetectionLogEntity detectionLog) {

        if (detectionLog.getId_room() == null) {
            throw new IllegalArgumentException(
                    "ID room wajib diisi"
            );
        }

        if (detectionLog.getMotion_detected() == null) {
            throw new IllegalArgumentException(
                    "Motion detected wajib diisi"
            );
        }

        if (detectionLog.getHuman_detected() == null) {
            throw new IllegalArgumentException(
                    "Human detected wajib diisi"
            );
        }

        if (detectionLog.getAction_taken() == null) {
            throw new IllegalArgumentException(
                    "Action taken wajib diisi"
            );
        }
    }
}
