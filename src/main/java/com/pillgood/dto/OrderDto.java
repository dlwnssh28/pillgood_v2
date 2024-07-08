package com.pillgood.dto;

import com.pillgood.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Integer ownedCouponId; // Integer로 변경
    private String orderStatus;
    private boolean subscriptionStatus;
    private List<OrderDetailDto> orderDetails = new ArrayList<>(); // 초기화

    // 기존 필드만 포함한 생성자
    public OrderDto(String orderNo, int totalAmount, String orderRequest, LocalDateTime orderDate, String recipient,
                    String postalCode, String address, String detailedAddress, String phoneNumber, String memberUniqueId,
                    Integer ownedCouponId, String orderStatus, boolean subscriptionStatus) {
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.orderRequest = orderRequest;
        this.orderDate = orderDate;
        this.recipient = recipient;
        this.postalCode = postalCode;
        this.address = address;
        this.detailedAddress = detailedAddress;
        this.phoneNumber = phoneNumber;
        this.memberUniqueId = memberUniqueId;
        this.ownedCouponId = ownedCouponId;
        this.orderStatus = orderStatus;
        this.subscriptionStatus = subscriptionStatus;
        this.orderDetails = new ArrayList<>(); // 초기화
    }

    // Order 엔티티를 인자로 받는 생성자 추가
    public OrderDto(Order order) {
        this.orderNo = order.getOrderNo();
        this.totalAmount = order.getTotalAmount();
        this.orderRequest = order.getOrderRequest();
        this.orderDate = order.getOrderDate();
        this.recipient = order.getRecipient();
        this.postalCode = order.getPostalCode();
        this.address = order.getAddress();
        this.detailedAddress = order.getDetailedAddress();
        this.phoneNumber = order.getPhoneNumber();
        this.memberUniqueId = order.getMemberUniqueId();
        this.ownedCouponId = order.getOwnedCouponId();
        this.orderStatus = order.getOrderStatus();
        this.subscriptionStatus = order.isSubscriptionStatus();
        this.orderDetails = new ArrayList<>(); // 초기화
    }
}
