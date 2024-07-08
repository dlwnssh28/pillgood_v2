package com.pillgood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderNo;
    private int totalAmount;
    private String orderRequest;
    private LocalDateTime orderDate;
    private String recipient;
    private String postalCode;
    private String address;
    private String detailedAddress;
    private String phoneNumber;
    private String memberUniqueId;
    private Integer ownedCouponId;
    private String orderStatus;
    private boolean subscriptionStatus;
    
}
