package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentCancelResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private String detail;
    private String refundStatus;
    private String requestedAt;
    private Cancels[] cancels; //환불 정보
    private int cancelAmount;
    private EasyPay easypays;
    private String method;
}
