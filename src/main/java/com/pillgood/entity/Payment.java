package com.pillgood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_no", length = 50)
    private String paymentNo;

    @Column(name = "order_no", nullable = false, length = 50)
    private String orderNo;
    
    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 20)
    private String method;

    @Column(name = "payment_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(name = "refund_status", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'NOT_REFUNDED'")
    private String refundStatus;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "subscription_status", nullable = false)
    private boolean subscriptionStatus;

    @ManyToOne
    @JoinColumn(name = "order_no", referencedColumnName = "order_no", insertable = false, updatable = false)
    private Order order;
}
