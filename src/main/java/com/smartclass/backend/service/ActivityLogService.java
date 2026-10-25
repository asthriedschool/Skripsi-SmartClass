package com.smartclass.backend.service;

import com.smartclass.backend.entity.ActivityLogEntity;
import com.smartclass.backend.entity.enumsActivityLogEntity.Source;
import com.smartclass.backend.repository.ActivityLogRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // GET semua activity log
    public List<ActivityLogEntity> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    // GET activity log berdasarkan ID
    public ActivityLogEntity getActivityLogById(Long id_activity_log) {

        if (id_activity_log == null) {
            throw new IllegalArgumentException(
                    "ID activity log tidak boleh null"
            );
        }

        return activityLogRepository.findById(id_activity_log)
                .orElseThrow(() -> new RuntimeException(
                        "Activity log dengan ID " + id_activity_log + " tidak ditemukan"
                ));
    }

    // CREATE activity log
    public ActivityLogEntity createActivityLog(ActivityLogEntity activityLog) {

        if (activityLog == null) {
            throw new IllegalArgumentException(
                    "Data activity log tidak boleh null"
            );
        }

        validateActivityLog(activityLog);

        return activityLogRepository.save(activityLog);
    }

    // UPDATE activity log
    public ActivityLogEntity updateActivityLog(
            Long id_activity_log,
            ActivityLogEntity activityLogData
    ) {

        if (id_activity_log == null) {
            throw new IllegalArgumentException(
                    "ID activity log tidak boleh null"
            );
        }

        if (activityLogData == null) {
            throw new IllegalArgumentException(
                    "Data activity log tidak boleh null"
            );
        }

        ActivityLogEntity activityLog = activityLogRepository.findById(id_activity_log)
                .orElseThrow(() -> new RuntimeException(
                        "Activity log dengan ID " + id_activity_log + " tidak ditemukan"
                ));

        validateActivityLog(activityLogData);

        activityLog.setId_user(activityLogData.getId_user());
        activityLog.setId_room(activityLogData.getId_room());
        activityLog.setId_device(activityLogData.getId_device());
        activityLog.setId_schedule(activityLogData.getId_schedule());
        activityLog.setActivity_type(activityLogData.getActivity_type());
        activityLog.setDescription(activityLogData.getDescription());
        activityLog.setSource(activityLogData.getSource());

        return activityLogRepository.save(activityLog);
    }

    // DELETE activity log
    public void deleteActivityLog(Long id_activity_log) {

        if (id_activity_log == null) {
            throw new IllegalArgumentException(
                    "ID activity log tidak boleh null"
            );
        }

        if (!activityLogRepository.existsById(id_activity_log)) {
            throw new RuntimeException(
                    "Activity log dengan ID " + id_activity_log + " tidak ditemukan"
            );
        }

        activityLogRepository.deleteById(id_activity_log);
    }

    // UPDATE source
    public ActivityLogEntity updateSource(
            Long id_activity_log,
            Source source
    ) {

        ActivityLogEntity activityLog = getActivityLogById(id_activity_log);

        if (source == null) {
            throw new IllegalArgumentException(
                    "Source tidak boleh null"
            );
        }

        activityLog.setSource(source);

        return activityLogRepository.save(activityLog);
    }

    // Validasi data activity log
    private void validateActivityLog(ActivityLogEntity activityLog) {

        if (activityLog.getActivity_type() == null ||
                activityLog.getActivity_type().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Tipe activity wajib diisi"
            );
        }

        if (activityLog.getDescription() == null ||
                activityLog.getDescription().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Description wajib diisi"
            );
        }

        if (activityLog.getSource() == null) {
            throw new IllegalArgumentException(
                    "Source wajib diisi"
            );
        }
    }
}
