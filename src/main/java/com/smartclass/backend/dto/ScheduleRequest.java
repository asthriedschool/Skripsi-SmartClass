package com.smartclass.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartclass.backend.entity.enumsScheduleEntity.DayOfWeek;
import com.smartclass.backend.entity.enumsScheduleEntity.ScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequest(
        @JsonProperty("id_room") Long idRoom,
        @JsonProperty("activity_name") String activityName,
        @JsonProperty("lecturer_name") String lecturerName,
        @JsonProperty("requester_name") String requesterName,
        @JsonProperty("schedule_type") ScheduleType scheduleType,
        @JsonProperty("schedule_date") LocalDate scheduleDate,
        @JsonProperty("day_of_week") DayOfWeek dayOfWeek,
        @JsonProperty("start_time") LocalTime startTime,
        @JsonProperty("end_time") LocalTime endTime,
        @JsonProperty("is_cancelled") Boolean isCancelled
) {
}
