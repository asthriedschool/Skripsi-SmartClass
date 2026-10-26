package com.smartclass.backend.service;

import com.smartclass.backend.entity.ScheduleEntity;
import com.smartclass.backend.entity.UserEntity;
import com.smartclass.backend.repository.ScheduleRepository;
import com.smartclass.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            UserRepository userRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    // GET semua schedule
    public List<ScheduleEntity> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // GET schedule berdasarkan ID
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

    // CREATE schedule
    public ScheduleEntity createSchedule(
            ScheduleEntity schedule,
            String userEmail
    ) {

        /*
         * =========================================================
         * PSEUDOCODE CREATE SCHEDULE
         * =========================================================
         *
         * 1. Pastikan data schedule tidak null.
         *
         * 2. Pastikan email user dari JWT tersedia.
         *
         * 3. Cari user berdasarkan email.
         *
         * 4. Ambil id_user dari user yang sedang login.
         *
         * 5. Jangan percaya created_by yang dikirim client.
         *
         * 6. Set:
         *
         *      schedule.created_by = user.id_user
         *
         * 7. Validasi schedule.
         *
         * 8. Simpan schedule ke database.
         *
         * Dengan cara ini user tidak bisa memalsukan
         * siapa yang membuat schedule.
         *
         * =========================================================
         */

        if (schedule == null) {
            throw new IllegalArgumentException(
                    "Data schedule tidak boleh null"
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

        schedule.setCreated_by(user.getId_user());

        validateSchedule(schedule);

        return scheduleRepository.save(schedule);
    }

    // UPDATE schedule
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

        schedule.setId_room(scheduleData.getId_room());

        /*
         * created_by TIDAK diubah ketika update.
         *
         * User yang pertama kali membuat schedule
         * tetap menjadi created_by.
         */
        schedule.setActivity_name(
                scheduleData.getActivity_name()
        );

        schedule.setLecturer_name(
                scheduleData.getLecturer_name()
        );

        schedule.setSchedule_type(
                scheduleData.getSchedule_type()
        );

        schedule.setSchedule_date(
                scheduleData.getSchedule_date()
        );

        schedule.setDay_of_week(
                scheduleData.getDay_of_week()
        );

        schedule.setStart_time(
                scheduleData.getStart_time()
        );

        schedule.setEnd_time(
                scheduleData.getEnd_time()
        );

        schedule.setIs_cancelled(
                scheduleData.getIs_cancelled()
        );

        return scheduleRepository.save(schedule);
    }

    // DELETE schedule
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

    // UPDATE is cancelled
    public ScheduleEntity updateCancelled(
            Long id_schedule,
            Boolean isCancelled
    ) {

        ScheduleEntity schedule =
                getScheduleById(id_schedule);

        if (isCancelled == null) {
            throw new IllegalArgumentException(
                    "Is cancelled tidak boleh null"
            );
        }

        schedule.setIs_cancelled(isCancelled);

        return scheduleRepository.save(schedule);
    }

    // Validasi data schedule
    private void validateSchedule(
            ScheduleEntity schedule
    ) {

        if (schedule.getId_room() == null) {
            throw new IllegalArgumentException(
                    "ID room wajib diisi"
            );
        }

        if (schedule.getActivity_name() == null ||
                schedule.getActivity_name().trim().isEmpty()) {

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

        if (schedule.getIs_cancelled() == null) {
            throw new IllegalArgumentException(
                    "Is cancelled wajib diisi"
            );
        }
    }
}