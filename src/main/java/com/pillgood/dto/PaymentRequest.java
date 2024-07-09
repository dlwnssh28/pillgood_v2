package com.pillgood.dto;

import lombok.Data;

@Data
public class PaymentRequest {
	private int amount;
    private String orderId;
    private String orderName;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String successUrl;
}
