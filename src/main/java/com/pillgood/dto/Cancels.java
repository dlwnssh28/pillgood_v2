package com.pillgood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cancels {
    private Long cancelAmount;
    private String cancelReason;
    private Long taxFreeAmount;
    private Long taxExemptionAmount;
    private Long refundableAmount;
    private Long easyPayDiscountAmount;
    private String canceledAt;
    private String transactionKey;
}
