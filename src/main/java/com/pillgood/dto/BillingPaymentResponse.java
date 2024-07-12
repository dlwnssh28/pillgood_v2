package com.pillgood.dto;

import lombok.Data;

@Data
public class BillingPaymentResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private int totalAmount;
    private String method;
    private String orderName;

}
