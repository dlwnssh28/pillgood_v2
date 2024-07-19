package com.pillgood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pillgood.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

	Payment findByPaymentNo(String paymentKey);
    Optional<Payment> findByOrderNo(String orderNo);

}
