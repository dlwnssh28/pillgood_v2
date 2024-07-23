package com.pillgood.controller;

import com.pillgood.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/total-sales")
    public List<Object[]> getTotalSales() {
        return dashboardService.getTotalSales();
    }

    @GetMapping("/daily-sales")
    public List<Object[]> getDailySales() {
        return dashboardService.getDailySales(LocalDateTime.now());
    }

    @GetMapping("/total-subscribers")
    public List<Object[]> getTotalSubscribers() {
        return dashboardService.getTotalSubscribers();
    }

    @GetMapping("/daily-subscribers")
    public List<Object[]> getDailySubscribers() {
        return dashboardService.getDailySubscribers(LocalDateTime.now());
    }

    @GetMapping("/monthly-sales")
    public List<Object[]> getMonthlySales() {
        return dashboardService.getMonthlySales();
    }
}
