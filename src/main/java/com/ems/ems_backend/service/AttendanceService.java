package com.ems.ems_backend.service;

import com.ems.ems_backend.event.AttendanceDeleted;
import com.ems.ems_backend.event.AttendanceMarked;
import com.ems.ems_backend.event.AttendanceUpdated;
import com.ems.ems_backend.exception.ForbiddenException;
import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Attendance;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.model.Role;
import com.ems.ems_backend.model.User;
import com.ems.ems_backend.repository.AttendanceRepository;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Attendance markAttendance(Long employeeId, LocalDate date, String status,
                                     LocalTime checkInTime, LocalTime checkOutTime, String username) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        enforceDepartmentScope(username, employee);

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

        Attendance saved = attendanceRepository.save(attendance);

        eventPublisher.publishEvent(new AttendanceMarked(
                saved.getId(), employeeId, saved.getDate(), saved.getStatus().name(), Instant.now()));

        return saved;
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

    @Transactional
    public Attendance updateAttendance(Long id, String status, LocalTime checkInTime, LocalTime checkOutTime,
                                       String username) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));
        enforceDepartmentScope(username, attendance.getEmployee());

        attendance.setStatus(com.ems.ems_backend.model.AttendanceStatus.valueOf(status.toUpperCase()));
        if (checkInTime != null) attendance.setCheckInTime(checkInTime);
        if (checkOutTime != null) attendance.setCheckOutTime(checkOutTime);
        Attendance saved = attendanceRepository.save(attendance);

        eventPublisher.publishEvent(new AttendanceUpdated(
                saved.getId(), saved.getEmployee().getId(), saved.getDate(), saved.getStatus().name(), Instant.now()));

        return saved;
    }

    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + id));

        Long employeeId = attendance.getEmployee().getId();
        attendanceRepository.deleteById(id);

        eventPublisher.publishEvent(new AttendanceDeleted(id, employeeId, Instant.now()));
    }

    /**
     * TEAM_LEAD can only mark/update attendance for employees within their own
     * department. Every other role manages company-wide, unaffected by this check.
     */
    private void enforceDepartmentScope(String username, Employee targetEmployee) {
        User requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (requestingUser.getRole() != Role.TEAM_LEAD) {
            return;
        }

        Long teamLeadDeptId = requestingUser.getEmployee() != null && requestingUser.getEmployee().getDepartment() != null
                ? requestingUser.getEmployee().getDepartment().getId()
                : null;

        if (teamLeadDeptId == null) {
            throw new ForbiddenException("Your account isn't assigned to a department. Contact admin.");
        }

        Long targetDeptId = targetEmployee.getDepartment() != null ? targetEmployee.getDepartment().getId() : null;
        if (!teamLeadDeptId.equals(targetDeptId)) {
            throw new ForbiddenException("You can only manage attendance for employees in your own department");
        }
    }
}
