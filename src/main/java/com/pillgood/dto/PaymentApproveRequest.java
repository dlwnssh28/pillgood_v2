package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentApproveRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
    private boolean subscriptionStatus;
}
