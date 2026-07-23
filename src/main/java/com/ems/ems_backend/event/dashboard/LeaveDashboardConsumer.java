package com.ems.ems_backend.event.dashboard;

import com.ems.ems_backend.event.LeaveApplied;
import com.ems.ems_backend.event.LeaveApproved;
import com.ems.ems_backend.event.LeaveRejected;
import com.ems.ems_backend.event.LeaveDeleted;
import com.ems.ems_backend.model.LeaveStatus;
import com.ems.ems_backend.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * leave-events carries three event types, so this uses the same
 * class-level @KafkaListener + @KafkaHandler dispatch as
 * LeaveNotificationConsumer / LeaveAuditConsumer.
 */
@Component
@KafkaListener(topics = "leave-events", groupId = "dashboard-service")
@RequiredArgsConstructor
@Slf4j
public class LeaveDashboardConsumer {

    private final LeaveRequestRepository leaveRequestRepository;

    @KafkaHandler
    public void onLeaveApplied(LeaveApplied event) {
        logPendingCount(event.employeeId());
    }

    @KafkaHandler
    public void onLeaveApproved(LeaveApproved event) {
        logPendingCount(event.employeeId());
    }

    @KafkaHandler
    public void onLeaveRejected(LeaveRejected event) {
        logPendingCount(event.employeeId());
    }

    @KafkaHandler
    public void onLeaveDeleted(LeaveDeleted event) {
        logPendingCount(event.employeeId());
    }

    private void logPendingCount(Long employeeId) {
        long pending = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        log.info("[dashboard-service] Pending leave approvals now {} (triggered by employeeId={})", pending, employeeId);
    }
}
