package com.pillgood.dto;

import lombok.Data;

@Data
public class BillingPaymentRequest {
    private String customerKey;
    private String orderId;
    private String orderName;
    private int amount;
    private String customerEmail;
    private String customerName;
}
