package com.pillgood.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillgood.repository.PaymentRepository;

@Service
public class DashboardService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Object[]> getTotalSales() {
        return paymentRepository.findTotalSales();
    }

    public List<Object[]> getDailySales(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);
        return paymentRepository.findDailySales(startOfDay, endOfDay);
    }

    public List<Object[]> getTotalSubscribers() {
        return paymentRepository.findTotalSubscribers();
    }

    public List<Object[]> getDailySubscribers(LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(LocalTime.MAX);
        return paymentRepository.findDailySubscribers(startOfDay, endOfDay);
    }

    public List<Object[]> getMonthlySales() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = now.plusMonths(1).withDayOfMonth(1).toLocalDate().atStartOfDay();
        return paymentRepository.findMonthlySales(startOfMonth, endOfMonth);
    }
}
