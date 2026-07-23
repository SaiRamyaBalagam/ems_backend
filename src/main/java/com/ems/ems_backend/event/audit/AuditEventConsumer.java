package com.ems.ems_backend.event.audit;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.event.SalaryPaid;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Stands in for a separate audit microservice. Subscribes to every EMS
 * topic under its own consumer group so it builds a full history
 * independently of notification-service and dashboard-service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "employee-events", groupId = "audit-service")
    public void onEmployeeEvent(EmployeeCreatedEvent event) throws Exception {
        AuditLog entry = new AuditLog();
        entry.setTopic("employee-events");
        entry.setEventType(EmployeeCreatedEvent.class.getSimpleName());
        entry.setEmployeeId(event.employeeId());
        entry.setPayload(objectMapper.writeValueAsString(event));
        entry.setOccurredAt(event.occurredAt());
        entry.setRecordedAt(Instant.now());
        auditLogRepository.save(entry);
        log.info("[audit-service] Recorded {} for employeeId={}", entry.getEventType(), event.employeeId());
    }

    @KafkaListener(topics = "salary-events", groupId = "audit-service")
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
}
