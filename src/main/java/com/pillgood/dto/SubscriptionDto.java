package com.pillgood.dto;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<OrderDetailDto> orderDetails;  // 추가

    // 모든 필드를 포함하는 생성자
    public SubscriptionDto(int subscriptionId, String memberUniqueId, LocalDateTime startDate, LocalDateTime endDate, String subscriptionStatus, String paymentNo) {
        this.subscriptionId = subscriptionId;
        this.memberUniqueId = memberUniqueId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subscriptionStatus = subscriptionStatus;
        this.paymentNo = paymentNo;
    }
}
