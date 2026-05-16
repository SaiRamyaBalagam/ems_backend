package com.ems.ems_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private long totalEmployees;
    private long presentToday;
    private long absentToday;
    private long pendingLeaves;
    private BigDecimal monthlyPayroll;
}
