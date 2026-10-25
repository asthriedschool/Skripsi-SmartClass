package com.smartclass.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleModel {
    private Long id_schedule;
    private Long id_room;
    private Long created_by;
    private String activity_name;
    private String lecturer_name;
    private String schedule_type;
    private LocalDate schedule_date;
    private String day_of_week;
    private LocalTime start_time;
    private LocalTime end_time;
    private Boolean is_cancelled;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
