package com.ems.ems_backend.event.dashboard;

import com.ems.ems_backend.event.AttendanceDeleted;
import com.ems.ems_backend.event.AttendanceMarked;
import com.ems.ems_backend.event.AttendanceUpdated;
import com.ems.ems_backend.model.AttendanceStatus;
import com.ems.ems_backend.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * attendance-events carries three event types from the start, so this uses
 * the same class-level @KafkaListener + @KafkaHandler dispatch as the other
 * dashboard consumers.
 */
@Component
@KafkaListener(topics = "attendance-events", groupId = "dashboard-service")
@RequiredArgsConstructor
@Slf4j
public class AttendanceDashboardConsumer {

    private final AttendanceRepository attendanceRepository;

    @KafkaHandler
    public void onAttendanceMarked(AttendanceMarked event) {
        logTodayStats(event.date(), event.employeeId());
    }

    @KafkaHandler
    public void onAttendanceUpdated(AttendanceUpdated event) {
        logTodayStats(event.date(), event.employeeId());
    }

    @KafkaHandler
    public void onAttendanceDeleted(AttendanceDeleted event) {
        // Deleted record's date isn't carried on the event, so just log the trigger
        log.info("[dashboard-service] Attendance record {} removed (employeeId={})",
                event.attendanceId(), event.employeeId());
    }

    private void logTodayStats(LocalDate date, Long employeeId) {
        var records = attendanceRepository.findByDate(date);
        long present = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();
        long absent = records.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();
        log.info("[dashboard-service] {} present, {} absent for {} (triggered by employeeId={})",
                present, absent, date, employeeId);
    }
}
