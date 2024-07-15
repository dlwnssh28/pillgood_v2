package com.pillgood.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private String paymentNo;
    private String orderNo;
    private int amount;
    private String status;
    private String method;
    private LocalDateTime paymentDate;
    private String detail;
    private String refundStatus;
    private LocalDateTime refundDate;
}
