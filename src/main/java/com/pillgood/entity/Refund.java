package com.pillgood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private int refundId;

    @Column(name = "refund_request_date")
    private LocalDateTime refundRequestDate;

    @Column(name = "refund_complete_date")
    private LocalDateTime refundCompleteDate;

    @Column(name = "total_refund_amount")
    private int totalRefundAmount;

    @Column(name = "refund_method", length = 50)
    private String refundMethod;

    @Column(name = "refund_bank", length = 50)
    private String refundBank;

    @Column(name = "refund_status", length = 50)
    private String refundStatus;

    @ManyToOne
    @JoinColumn(name = "order_no", referencedColumnName = "order_no")
    private Order order;
}
