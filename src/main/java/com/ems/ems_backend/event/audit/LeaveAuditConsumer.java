package com.ems.ems_backend.event.audit;

import com.ems.ems_backend.event.LeaveApplied;
import com.ems.ems_backend.event.LeaveApproved;
import com.ems.ems_backend.event.LeaveRejected;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@KafkaListener(topics = "leave-events", groupId = "audit-service")
@RequiredArgsConstructor
@Slf4j
public class LeaveAuditConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaHandler
    public void onLeaveApplied(LeaveApplied event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    @KafkaHandler
    public void onLeaveApproved(LeaveApproved event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    @KafkaHandler
    public void onLeaveRejected(LeaveRejected event) throws Exception {
        record(event.employeeId(), event.occurredAt(), event);
    }

    private void record(Long employeeId, Instant occurredAt, Object event) throws Exception {
        AuditLog entry = new AuditLog();
        entry.setTopic("leave-events");
        entry.setEventType(event.getClass().getSimpleName());
        entry.setEmployeeId(employeeId);
        entry.setPayload(objectMapper.writeValueAsString(event));
        entry.setOccurredAt(occurredAt);
        entry.setRecordedAt(Instant.now());
        auditLogRepository.save(entry);
        log.info("[audit-service] Recorded {} for employeeId={}", entry.getEventType(), employeeId);
    }
}
