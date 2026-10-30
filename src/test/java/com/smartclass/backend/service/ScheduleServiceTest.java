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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        scheduleService = new ScheduleService(
                scheduleRepository,
                roomRepository,
                userRepository
        );
    }

    @Test
    void petugasCreationSetsCreatorAndApprovalFromAuthenticatedUser() {
        UserEntity petugas = user(42L, Role.PETUGAS);
        when(userRepository.findByEmail("petugas@example.com"))
                .thenReturn(Optional.of(petugas));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.findActiveOverlappingSchedules(
                5L, LocalTime.of(9, 0), LocalTime.of(10, 0), null
        )).thenReturn(List.of());
        when(scheduleRepository.save(any(ScheduleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleEntity result = scheduleService.createSchedule(
                request(ScheduleType.ONCE, "Pemohon"),
                "petugas@example.com"
        );

        assertEquals(42L, result.getCreated_by());
        assertEquals(42L, result.getApproved_by());
        assertNotNull(result.getApproved_at());
        assertEquals("Pemohon", result.getRequester_name());
    }

    @Test
    void rejectsPetugasOnceCreationWithoutRequesterName() {
        when(userRepository.findByEmail("petugas@example.com"))
                .thenReturn(Optional.of(user(42L, Role.PETUGAS)));
        when(roomRepository.existsById(5L)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> scheduleService.createSchedule(
                        request(ScheduleType.ONCE, null),
                        "petugas@example.com"
                )
        );
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    @Test
    void adminCanCreateOnceScheduleWithoutRequesterName() {
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(user(10L, Role.ADMIN)));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.findActiveOverlappingSchedules(
                5L, LocalTime.of(9, 0), LocalTime.of(10, 0), null
        )).thenReturn(List.of());
        when(scheduleRepository.save(any(ScheduleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleEntity result = scheduleService.createSchedule(
                request(ScheduleType.ONCE, null),
                "admin@example.com"
        );

        assertEquals(10L, result.getCreated_by());
        assertNull(result.getRequester_name());
        assertNull(result.getApproved_by());
        assertNull(result.getApproved_at());
    }

    @Test
    void rejectsUpdateWhenWeeklyScheduleConflictsWithOnceSchedule() {
        ScheduleEntity once = schedule(
                20L,
                ScheduleType.ONCE,
                LocalDate.of(2026, 11, 1),
                null,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                false
        );
        when(scheduleRepository.findById(7L))
                .thenReturn(Optional.of(schedule(
                        7L,
                        ScheduleType.WEEKLY,
                        null,
                        DayOfWeek.SUNDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        false
                )));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.findActiveOverlappingSchedules(
                5L, LocalTime.of(10, 0), LocalTime.of(11, 0), 7L
        )).thenReturn(List.of(once));

        assertThrows(
                ScheduleConflictException.class,
                () -> scheduleService.updateSchedule(
                        7L,
                        schedule(
                                null,
                                ScheduleType.WEEKLY,
                                null,
                                DayOfWeek.SUNDAY,
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 0),
                                false
                        )
                )
        );
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    @Test
    void rejectsOnceUpdateWhenWeeklyScheduleConflicts() {
        ScheduleEntity weekly = schedule(
                20L,
                ScheduleType.WEEKLY,
                null,
                DayOfWeek.SUNDAY,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                false
        );
        when(scheduleRepository.findById(7L))
                .thenReturn(Optional.of(schedule(
                        7L,
                        ScheduleType.ONCE,
                        LocalDate.of(2026, 11, 1),
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        false
                )));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.findActiveOverlappingSchedules(
                5L, LocalTime.of(10, 0), LocalTime.of(11, 0), 7L
        )).thenReturn(List.of(weekly));

        assertThrows(
                ScheduleConflictException.class,
                () -> scheduleService.updateSchedule(
                        7L,
                        schedule(
                                null,
                                ScheduleType.ONCE,
                                LocalDate.of(2026, 11, 1),
                                null,
                                LocalTime.of(10, 0),
                                LocalTime.of(11, 0),
                                false
                        )
                )
        );
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    @Test
    void cancelledUpdateSkipsConflictCheckAndPreservesRequesterName() {
        ScheduleEntity current = schedule(
                7L,
                ScheduleType.ONCE,
                LocalDate.of(2026, 11, 1),
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                false
        );
        current.setRequester_name("Nama tersimpan");
        when(scheduleRepository.findById(7L)).thenReturn(Optional.of(current));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.save(any(ScheduleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleEntity result = scheduleService.updateSchedule(
                7L,
                schedule(
                        null,
                        ScheduleType.ONCE,
                        LocalDate.of(2026, 11, 1),
                        null,
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        true
                )
        );

        assertEquals("Nama tersimpan", result.getRequester_name());
        verify(scheduleRepository, never()).findActiveOverlappingSchedules(
                any(), any(), any(), any()
        );
    }

    @Test
    void adjacentUpdateDoesNotConflictAndExcludesCurrentSchedule() {
        ScheduleEntity current = schedule(
                7L,
                ScheduleType.ONCE,
                LocalDate.of(2026, 11, 1),
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                false
        );
        when(scheduleRepository.findById(7L)).thenReturn(Optional.of(current));
        when(roomRepository.existsById(5L)).thenReturn(true);
        when(scheduleRepository.findActiveOverlappingSchedules(
                5L, LocalTime.of(10, 0), LocalTime.of(11, 0), 7L
        )).thenReturn(List.of());
        when(scheduleRepository.save(any(ScheduleEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleEntity result = scheduleService.updateSchedule(
                7L,
                schedule(
                        null,
                        ScheduleType.ONCE,
                        LocalDate.of(2026, 11, 1),
                        null,
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        false
                )
        );

        assertEquals(LocalTime.of(10, 0), result.getStart_time());
        verify(scheduleRepository).findActiveOverlappingSchedules(
                5L, LocalTime.of(10, 0), LocalTime.of(11, 0), 7L
        );
    }

    private UserEntity user(Long id, Role role) {
        UserEntity user = new UserEntity();
        user.setId_user(id);
        user.setRole(role);
        return user;
    }

    private ScheduleRequest request(ScheduleType type, String requesterName) {
        return new ScheduleRequest(
                5L,
                "Rapat",
                null,
                requesterName,
                type,
                type == ScheduleType.ONCE ? LocalDate.of(2026, 11, 1) : null,
                type == ScheduleType.WEEKLY ? DayOfWeek.SUNDAY : null,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                false
        );
    }

    private ScheduleEntity schedule(
            Long id,
            ScheduleType type,
            LocalDate date,
            DayOfWeek day,
            LocalTime start,
            LocalTime end,
            boolean cancelled
    ) {
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId_schedule(id);
        schedule.setId_room(5L);
        schedule.setActivity_name("Rapat");
        schedule.setSchedule_type(type);
        schedule.setSchedule_date(date);
        schedule.setDay_of_week(day);
        schedule.setStart_time(start);
        schedule.setEnd_time(end);
        schedule.setIs_cancelled(cancelled);
        return schedule;
    }
}
