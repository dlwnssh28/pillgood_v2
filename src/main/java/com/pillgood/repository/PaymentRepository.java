package com.pillgood.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pillgood.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByPaymentNo(String paymentNo);
    Optional<Payment> findByOrderNo(String orderNo);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m') AS date, SUM(p.amount) AS value " +
           "FROM Payment p WHERE p.status = 'DONE' GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m')")
    List<Object[]> findTotalSales();

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m') AS date, COUNT(p) AS value " +
           "FROM Payment p WHERE p.status = 'DONE' AND p.subscriptionStatus = 'BILLING' " +
           "GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m')")
    List<Object[]> findTotalSubscribers();

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d') AS date, SUM(p.amount) AS value " +
           "FROM Payment p WHERE p.status = 'DONE' AND p.paymentDate >= :startOfMonth AND p.paymentDate < :endOfMonth " +
           "GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d')")
    List<Object[]> findMonthlySales(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d %H:00') AS date, SUM(p.amount) AS value " +
           "FROM Payment p WHERE p.status = 'DONE' AND p.paymentDate >= :startOfDay AND p.paymentDate < :endOfDay " +
           "GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d %H:00')")
    List<Object[]> findDailySales(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d %H:00') AS date, COUNT(p) AS value " +
           "FROM Payment p WHERE p.status = 'DONE' AND p.subscriptionStatus = 'BILLING' " +
           "AND p.paymentDate >= :startOfDay AND p.paymentDate < :endOfDay " +
           "GROUP BY FUNCTION('DATE_FORMAT', p.paymentDate, '%Y-%m-%d %H:00')")
    List<Object[]> findDailySubscribers(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
