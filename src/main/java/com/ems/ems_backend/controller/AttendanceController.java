package com.ems.ems_backend.controller;

import com.ems.ems_backend.dto.AttendanceRequest;
import com.ems.ems_backend.model.Attendance;
import com.ems.ems_backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Attendance> markAttendance(@RequestBody AttendanceRequest request) {
        Attendance attendance = attendanceService.markAttendance(
                request.getEmployeeId(), request.getDate(), request.getStatus(),
                request.getCheckInTime(), request.getCheckOutTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Attendance>> getAttendanceByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployee(employeeId));
    }

    @GetMapping("/monthly/{employeeId}")
    public ResponseEntity<List<Attendance>> getMonthlyAttendance(
            @PathVariable Long employeeId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(attendanceService.getMonthlyAttendance(employeeId, month, year));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id,
                                                       @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(
                id, request.getStatus(), request.getCheckInTime(), request.getCheckOutTime()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok("Attendance record deleted successfully");
    }
}
