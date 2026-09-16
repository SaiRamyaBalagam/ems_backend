package com.ems.ems_backend.event.audit;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.event.EmployeeDeletedEvent;
import com.ems.ems_backend.event.EmployeeReactivatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * employee-events now carries two event types, so this is split out from
 * AuditEventConsumer into its own class-level @KafkaListener +
 * @KafkaHandler dispatch (same pattern as LeaveAuditConsumer) — a
 * class-level @KafkaListener can only target one topic.
 */
@Component
@KafkaListener(topics = "employee-events", groupId = "audit-service")
@RequiredArgsConstructor
@Slf4j
public class EmployeeAuditConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaHandler
    public void onEmployeeCreated(EmployeeCreatedEvent event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    @KafkaHandler
    public void onEmployeeDeleted(EmployeeDeletedEvent event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    @KafkaHandler
    public void onEmployeeReactivated(EmployeeReactivatedEvent event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    private void record(Long employeeId, Instant occurredAt, Object event) throws Exception {
        AuditLog entry = new AuditLog();
        entry.setTopic("employee-events");
        entry.setEventType(event.getClass().getSimpleName());
        entry.setEmployeeId(employeeId);
        entry.setPayload(objectMapper.writeValueAsString(event));
        entry.setOccurredAt(occurredAt);
        entry.setRecordedAt(Instant.now());
        auditLogRepository.save(entry);
        log.info("[audit-service] Recorded {} for employeeId={}", entry.getEventType(), employeeId);
    }
}
