package com.ems.ems_backend.event;

import java.time.Instant;

public record AttendanceDeleted(
        Long attendanceId,
        Long employeeId,
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
