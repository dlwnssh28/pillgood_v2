package com.pillgood.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private int subscriptionId;
    private String memberUniqueId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String subscriptionStatus;
    private String paymentNo;
}
