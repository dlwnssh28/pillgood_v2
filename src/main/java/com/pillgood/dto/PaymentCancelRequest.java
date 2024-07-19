package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
    private Integer cancelAmount; // Optional for partial cancel
    
}
