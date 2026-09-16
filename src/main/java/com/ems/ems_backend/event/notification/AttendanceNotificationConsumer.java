package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.AttendanceDeleted;
import com.ems.ems_backend.event.AttendanceMarked;
import com.ems.ems_backend.event.AttendanceUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * attendance-events carries three event types from the start, so this uses
 * the same class-level @KafkaListener + @KafkaHandler dispatch as the other
 * notification consumers.
 */
@Component
@KafkaListener(topics = "attendance-events", groupId = "notification-service")
@Slf4j
public class AttendanceNotificationConsumer {

    @KafkaHandler
    public void onAttendanceMarked(AttendanceMarked event) {
        log.info("[notification-service] Attendance marked {} for employeeId={} on {}",
                event.status(), event.employeeId(), event.date());
    }

    @KafkaHandler
    public void onAttendanceUpdated(AttendanceUpdated event) {
        log.info("[notification-service] Attendance updated to {} for employeeId={} on {}",
                event.status(), event.employeeId(), event.date());
    }

    @KafkaHandler
    public void onAttendanceDeleted(AttendanceDeleted event) {
        log.info("[notification-service] Attendance record {} deleted for employeeId={}",
                event.attendanceId(), event.employeeId());
    }
}
