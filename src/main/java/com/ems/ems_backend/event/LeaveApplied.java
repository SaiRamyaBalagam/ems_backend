package com.ems.ems_backend.event;

import java.time.Instant;
import java.time.LocalDate;

public record LeaveApplied(
        Long leaveId,
        Long employeeId,
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        Instant occurredAt
) implements EmsEvent {

    @Override
    public String topic() {
        return "leave-events";
    }

    @Override
    public String key() {
        return String.valueOf(employeeId);
    }
}
