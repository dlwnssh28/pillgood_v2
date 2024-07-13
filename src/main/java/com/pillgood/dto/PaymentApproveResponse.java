package com.pillgood.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentApproveResponse {
    private String paymentKey;
    private String orderId;
    private int totalAmount;
    private String method;
    private String status;
    private String type;
    private String approvedAt;
}