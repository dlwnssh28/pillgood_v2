package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentKey;
    private int amount;
}
