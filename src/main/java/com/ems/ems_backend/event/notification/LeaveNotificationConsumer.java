package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.LeaveApplied;
import com.ems.ems_backend.event.LeaveApproved;
import com.ems.ems_backend.event.LeaveRejected;
import com.ems.ems_backend.event.LeaveDeleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * leave-events carries three event types, so this uses Spring Kafka's
 * class-level @KafkaListener + @KafkaHandler dispatch (one listener
 * container, routed to the matching method by payload type).
 */
@Component
@KafkaListener(topics = "leave-events", groupId = "notification-service")
@Slf4j
public class LeaveNotificationConsumer {

    @KafkaHandler
    public void onLeaveApplied(LeaveApplied event) {
        log.info("[notification-service] Notify manager: leave applied by employeeId={} ({} to {})",
                event.employeeId(), event.startDate(), event.endDate());
    }

    @KafkaHandler
    public void onLeaveApproved(LeaveApproved event) {
        log.info("[notification-service] Notify employeeId={}: leave {} approved", event.employeeId(), event.leaveId());
    }

    @KafkaHandler
    public void onLeaveRejected(LeaveRejected event) {
        log.info("[notification-service] Notify employeeId={}: leave {} rejected", event.employeeId(), event.leaveId());
    }

    @KafkaHandler
    public void onLeaveDeleted(LeaveDeleted event) {
        log.info("[notification-service] Notify employeeId={}: leave {} deleted", event.employeeId(), event.leaveId());
    }
}
