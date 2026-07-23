package com.ems.ems_backend.event;

import java.math.BigDecimal;
import java.time.Instant;

public record SalaryDeleted(
        Long salaryId,
        Long employeeId,
        BigDecimal netSalary,
        int payMonth,
        int payYear,
        Instant occurredAt
) implements EmsEvent {
    @Override
    public String topic() {
        return "salary-events";
    }

    @Override
    public String key() {
        return String.valueOf(employeeId);
    }
}
