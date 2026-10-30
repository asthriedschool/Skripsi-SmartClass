package com.smartclass.backend.service;

import com.smartclass.backend.dto.ScheduleRequest;
import com.smartclass.backend.entity.ScheduleEntity;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.entity.enumsScheduleEntity.DayOfWeek;
import com.smartclass.backend.entity.enumsScheduleEntity.ScheduleType;
import com.smartclass.backend.entity.enumsUserEntity.Role;
import com.smartclass.backend.exception.ScheduleConflictException;
import com.smartclass.backend.repository.RoomRepository;
import com.smartclass.backend.repository.ScheduleRepository;
import com.smartclass.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            RoomRepository roomRepository,
            UserRepository userRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<ScheduleEntity> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public ScheduleEntity getScheduleById(Long id_schedule) {
        if (id_schedule == null) {
            throw new IllegalArgumentException(
                    "ID schedule tidak boleh null"
            );
        }

        return scheduleRepository.findById(id_schedule)
                .orElseThrow(() -> new RuntimeException(
                        "Schedule dengan ID " + id_schedule
                                + " tidak ditemukan"
                ));
    }

    public ScheduleEntity createSchedule(
            ScheduleRequest request,
            String userEmail
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Data schedule tidak boleh null"
            );
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "User yang login tidak ditemukan"
            );
        }

        UserEntity user = userRepository.findByEmail(userEmail.trim())
                .orElseThrow(() -> new RuntimeException(
                        "User dengan email " + userEmail
                                + " tidak ditemukan"
                ));

        if (user.getRole() == Role.PETUGAS
                && request.scheduleType() != ScheduleType.ONCE) {
            throw new IllegalArgumentException(
                    "Petugas hanya dapat membuat schedule insidental (ONCE)"
            );
        }

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId_room(request.idRoom());
        schedule.setActivity_name(request.activityName());
        schedule.setLecturer_name(request.lecturerName());
        schedule.setRequester_name(request.requesterName());
        schedule.setSchedule_type(request.scheduleType());
        schedule.setSchedule_date(request.scheduleDate());
        schedule.setDay_of_week(request.dayOfWeek());
        schedule.setStart_time(request.startTime());
        schedule.setEnd_time(request.endTime());
        schedule.setIs_cancelled(
                request.isCancelled() == null ? false : request.isCancelled()
        );
        schedule.setCreated_by(user.getId_user());

        validateSchedule(schedule);

        validateSchedulePattern(
                schedule,
                user.getRole() == Role.PETUGAS
        );

        if (user.getRole() == Role.PETUGAS && schedule.getIs_cancelled()) {
            throw new IllegalArgumentException(
                    "Petugas tidak dapat membuat schedule yang dibatalkan"
            );
        }

        if (!schedule.getIs_cancelled() && hasScheduleConflict(schedule, null)) {
            throw new ScheduleConflictException(
                    "Jadwal ruangan berbenturan dengan schedule aktif"
            );
        }

        if (user.getRole() == Role.PETUGAS) {
            schedule.setApproved_by(user.getId_user());
            schedule.setApproved_at(LocalDateTime.now());
        }

        return scheduleRepository.save(schedule);
    }

    public ScheduleEntity updateSchedule(
            Long id_schedule,
            ScheduleEntity scheduleData
    ) {
        if (id_schedule == null) {
            throw new IllegalArgumentException(
                    "ID schedule tidak boleh null"
            );
        }

        if (scheduleData == null) {
            throw new IllegalArgumentException(
                    "Data schedule tidak boleh null"
            );
        }

        ScheduleEntity schedule = scheduleRepository.findById(id_schedule)
                .orElseThrow(() -> new RuntimeException(
                        "Schedule dengan ID " + id_schedule
                                + " tidak ditemukan"
                ));

        validateSchedule(scheduleData);
        validateSchedulePattern(scheduleData, false);

        if (!scheduleData.getIs_cancelled()
                && hasScheduleConflict(scheduleData, id_schedule)) {
            throw new ScheduleConflictException(
                    "Jadwal ruangan berbenturan dengan schedule aktif"
            );
        }

        schedule.setId_room(scheduleData.getId_room());
        schedule.setActivity_name(scheduleData.getActivity_name());
        schedule.setLecturer_name(scheduleData.getLecturer_name());
        if (scheduleData.getRequester_name() != null) {
            schedule.setRequester_name(scheduleData.getRequester_name());
        }
        schedule.setSchedule_type(scheduleData.getSchedule_type());
        schedule.setSchedule_date(scheduleData.getSchedule_date());
        schedule.setDay_of_week(scheduleData.getDay_of_week());
        schedule.setStart_time(scheduleData.getStart_time());
        schedule.setEnd_time(scheduleData.getEnd_time());
        schedule.setIs_cancelled(scheduleData.getIs_cancelled());

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id_schedule) {
        if (id_schedule == null) {
            throw new IllegalArgumentException(
                    "ID schedule tidak boleh null"
            );
        }

        if (!scheduleRepository.existsById(id_schedule)) {
            throw new RuntimeException(
                    "Schedule dengan ID " + id_schedule
                            + " tidak ditemukan"
            );
        }

        scheduleRepository.deleteById(id_schedule);
    }

    public ScheduleEntity updateCancelled(
            Long id_schedule,
            Boolean isCancelled
    ) {
        ScheduleEntity schedule = getScheduleById(id_schedule);

        if (isCancelled == null) {
            throw new IllegalArgumentException(
                    "Is cancelled tidak boleh null"
            );
        }

        schedule.setIs_cancelled(isCancelled);
        return scheduleRepository.save(schedule);
    }

    private void validateSchedule(ScheduleEntity schedule) {
        if (schedule.getId_room() == null) {
            throw new IllegalArgumentException(
                    "ID room wajib diisi"
            );
        }

        if (!roomRepository.existsById(schedule.getId_room())) {
            throw new IllegalArgumentException(
                    "Ruangan dengan ID " + schedule.getId_room()
                            + " tidak ditemukan"
            );
        }

        if (schedule.getActivity_name() == null
                || schedule.getActivity_name().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Nama activity wajib diisi"
            );
        }

        if (schedule.getSchedule_type() == null) {
            throw new IllegalArgumentException(
                    "Tipe schedule wajib diisi"
            );
        }

        if (schedule.getStart_time() == null) {
            throw new IllegalArgumentException(
                    "Start time wajib diisi"
            );
        }

        if (schedule.getEnd_time() == null) {
            throw new IllegalArgumentException(
                    "End time wajib diisi"
            );
        }

        if (!schedule.getStart_time().isBefore(schedule.getEnd_time())) {
            throw new IllegalArgumentException(
                    "Start time harus lebih awal daripada end time"
            );
        }

        if (schedule.getIs_cancelled() == null) {
            throw new IllegalArgumentException(
                    "Is cancelled wajib diisi"
            );
        }
    }

    private void validateOnceSchedule(ScheduleEntity schedule) {
        if (schedule.getSchedule_date() == null) {
            throw new IllegalArgumentException(
                    "Tanggal schedule ONCE wajib diisi"
            );
        }
        schedule.setDay_of_week(null);
    }

    private void validateSchedulePattern(
            ScheduleEntity schedule,
            boolean requesterRequired
    ) {
        if (schedule.getSchedule_type() == ScheduleType.ONCE) {
            validateOnceSchedule(schedule);
            if (requesterRequired) {
                validateRequesterName(schedule);
            }
        } else if (schedule.getDay_of_week() == null) {
            throw new IllegalArgumentException(
                    "Hari schedule WEEKLY wajib diisi"
            );
        }
    }

    private void validateRequesterName(ScheduleEntity schedule) {
        if (schedule.getRequester_name() == null
                || schedule.getRequester_name().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Nama pemohon wajib diisi"
            );
        }

        if (schedule.getRequester_name().length() > 100) {
            throw new IllegalArgumentException(
                    "Nama pemohon tidak boleh lebih dari 100 karakter"
            );
        }
    }

    private boolean hasScheduleConflict(
            ScheduleEntity candidate,
            Long excludedScheduleId
    ) {
        DayOfWeek candidateDay = candidate.getSchedule_type() == ScheduleType.ONCE
                ? DayOfWeek.valueOf(
                        candidate.getSchedule_date().getDayOfWeek().name()
                )
                : candidate.getDay_of_week();

        return scheduleRepository.findActiveOverlappingSchedules(
                        candidate.getId_room(),
                        candidate.getStart_time(),
                        candidate.getEnd_time(),
                        excludedScheduleId
                ).stream()
                .anyMatch(existing -> appliesOnCandidateDate(existing, candidate, candidateDay));
    }

    private boolean appliesOnCandidateDate(
            ScheduleEntity existing,
            ScheduleEntity candidate,
            DayOfWeek candidateDay
    ) {
        if (existing.getSchedule_type() == ScheduleType.WEEKLY) {
            return existing.getDay_of_week() == candidateDay;
        }

        if (existing.getSchedule_type() == ScheduleType.ONCE
                && existing.getSchedule_date() != null) {
            if (candidate.getSchedule_type() == ScheduleType.ONCE) {
                return existing.getSchedule_date().equals(candidate.getSchedule_date());
            }
            return DayOfWeek.valueOf(
                    existing.getSchedule_date().getDayOfWeek().name()
            ) == candidateDay;
        }

        return false;
    }
}
