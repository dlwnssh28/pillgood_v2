package com.pillgood.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pillgood.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
