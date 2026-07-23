package com.ems.ems_backend.event;

import java.time.Instant;

public record EmployeeDeletedEvent(
        Long employeeId,
        String name,
        String email,
        Long departmentId,
        String departmentName,
        Instant occurredAt
) implements EmsEvent {

    @Override
    public String topic() {
        return "employee-events";
    }

    @Override
    public String key() {
        return String.valueOf(employeeId);
    }
}
