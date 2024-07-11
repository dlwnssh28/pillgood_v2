package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentApproveResponse {
    private String paymentKey;
    private String orderId;
    private int totalAmount;
    private String method;
    private String status;
}