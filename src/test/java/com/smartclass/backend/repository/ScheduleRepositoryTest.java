package com.smartclass.backend.repository;

import com.smartclass.backend.entity.ScheduleEntity;
import com.smartclass.backend.entity.enumsScheduleEntity.DayOfWeek;
import com.smartclass.backend.entity.enumsScheduleEntity.ScheduleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    void findsOverlappingActiveSchedulesAndExcludesUpdatedSchedule() {
        saveSchedule(
                1L,
                ScheduleType.ONCE,
                LocalDate.of(2026, 11, 1),
                null,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                false
        );
        ScheduleEntity weekly = saveSchedule(
                1L,
                ScheduleType.WEEKLY,
                null,
                DayOfWeek.SUNDAY,
                LocalTime.of(10, 15),
                LocalTime.of(11, 15),
                false
        );

        List<ScheduleEntity> overlaps =
                scheduleRepository.findActiveOverlappingSchedules(
                        1L,
                        LocalTime.of(10, 30),
                        LocalTime.of(11, 30),
                        weekly.getId_schedule()
                );

        assertThat(overlaps)
                .extracting(ScheduleEntity::getSchedule_type)
                .containsExactly(ScheduleType.ONCE);
    }

    @Test
    void excludesCancelledAndAdjacentSchedules() {
        saveSchedule(
                2L,
                ScheduleType.ONCE,
                LocalDate.of(2026, 11, 1),
                null,
                LocalTime.of(10, 30),
                LocalTime.of(11, 30),
                true
        );
        saveSchedule(
                2L,
                ScheduleType.WEEKLY,
                null,
                DayOfWeek.SUNDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                false
        );

        assertThat(scheduleRepository.findActiveOverlappingSchedules(
                2L,
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                null
        )).isEmpty();
    }

    private ScheduleEntity saveSchedule(
            Long roomId,
            ScheduleType type,
            LocalDate date,
            DayOfWeek day,
            LocalTime start,
            LocalTime end,
            boolean cancelled
    ) {
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId_room(roomId);
        schedule.setActivity_name("Activity");
        schedule.setSchedule_type(type);
        schedule.setSchedule_date(date);
        schedule.setDay_of_week(day);
        schedule.setStart_time(start);
        schedule.setEnd_time(end);
        schedule.setIs_cancelled(cancelled);
        return scheduleRepository.saveAndFlush(schedule);
    }
}
