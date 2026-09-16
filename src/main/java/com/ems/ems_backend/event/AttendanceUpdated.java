package com.ems.ems_backend.event;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceUpdated(
        Long attendanceId,
        Long employeeId,
        LocalDate date,
        String status,
        Instant occurredAt
) implements EmsEvent {

    @Override
    public String topic() {
        return "attendance-events";
    }

    @Override
    public String key() {
        return String.valueOf(employeeId);
    }
}
