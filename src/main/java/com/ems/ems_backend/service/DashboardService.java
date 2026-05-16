package com.ems.ems_backend.service;

import com.ems.ems_backend.dto.DashboardResponse;
import com.ems.ems_backend.model.AttendanceStatus;
import com.ems.ems_backend.model.LeaveStatus;
import com.ems.ems_backend.repository.AttendanceRepository;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.LeaveRequestRepository;
import com.ems.ems_backend.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final SalaryRepository salaryRepository;

    public DashboardResponse getSummary() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        long totalEmployees = employeeRepository.count();

        long presentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();

        long absentToday = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();

        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);

        BigDecimal monthlyPayroll = salaryRepository.findByPayMonthAndPayYear(currentMonth, currentYear)
                .stream()
                .map(s -> s.getNetSalary())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(totalEmployees, presentToday, absentToday, pendingLeaves, monthlyPayroll);
    }
}
