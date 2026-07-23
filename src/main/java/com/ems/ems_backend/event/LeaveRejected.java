package com.ems.ems_backend.event;

import java.time.Instant;

public record LeaveRejected(
        Long leaveId,
        Long employeeId,
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
