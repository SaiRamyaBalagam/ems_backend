package com.ems.ems_backend.service;

import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Attendance;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.repository.AttendanceRepository;
import com.ems.ems_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public Attendance markAttendance(Long employeeId, LocalDate date, String status,
                                     LocalTime checkInTime, LocalTime checkOutTime) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (attendanceRepository.findByEmployeeIdAndDate(employeeId, date).isPresent()) {
            throw new DataIntegrityViolationException(
                    "Attendance already marked for employee " + employeeId + " on " + date);
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(date);
        attendance.setStatus(com.ems.ems_backend.model.AttendanceStatus.valueOf(status.toUpperCase()));
        attendance.setCheckInTime(checkInTime);
        attendance.setCheckOutTime(checkOutTime);

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> getMonthlyAttendance(Long employeeId, int month, int year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, start, end);
    }

    public Attendance updateAttendance(Long id, String status, LocalTime checkInTime, LocalTime checkOutTime) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        attendance.setStatus(com.ems.ems_backend.model.AttendanceStatus.valueOf(status.toUpperCase()));
        if (checkInTime != null) attendance.setCheckInTime(checkInTime);
        if (checkOutTime != null) attendance.setCheckOutTime(checkOutTime);
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }
}
