package com.ems.ems_backend.event.audit;

import com.ems.ems_backend.event.SalaryPaid;
import com.ems.ems_backend.event.SalaryDeleted;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Stands in for a separate audit microservice. employee-events moved out
 * to EmployeeAuditConsumer once it grew a second event type — this class
 * now only handles salary-events, which itself grew a second type
 * (SalaryDeleted), so this uses class-level @KafkaListener +
 * @KafkaHandler dispatch too.
 */
@Component
@KafkaListener(topics = "salary-events", groupId = "audit-service")
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaHandler
    public void onSalaryPaid(SalaryPaid event) throws Exception {
        AuditLog entry = new AuditLog();
        entry.setTopic("salary-events");
        entry.setEventType(SalaryPaid.class.getSimpleName());
        entry.setEmployeeId(event.employeeId());
        entry.setPayload(objectMapper.writeValueAsString(event));
        entry.setOccurredAt(event.occurredAt());
        entry.setRecordedAt(Instant.now());
        auditLogRepository.save(entry);
        log.info("[audit-service] Recorded {} for employeeId={}", entry.getEventType(), event.employeeId());
    }

    @KafkaHandler
    public void onSalaryDeleted(SalaryDeleted event) throws Exception {
        AuditLog entry = new AuditLog();
        entry.setTopic("salary-events");
        entry.setEventType(SalaryDeleted.class.getSimpleName());
        entry.setEmployeeId(event.employeeId());
        entry.setPayload(objectMapper.writeValueAsString(event));
        entry.setOccurredAt(event.occurredAt());
        entry.setRecordedAt(Instant.now());
        auditLogRepository.save(entry);
        log.info("[audit-service] Recorded {} for employeeId={}", entry.getEventType(), event.employeeId());
    }
}
