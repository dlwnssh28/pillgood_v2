package com.pillgood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnedcouponDto {
    private int ownedCouponId;
    private int couponId;
    private String memberUniqueId;
    private boolean couponUsed;
    private LocalDateTime issuedDate;
    private LocalDateTime expiryDate;
    private String couponName;
    private Integer discountAmount;
    
    public OwnedcouponDto(Integer ownedCouponId, Integer couponId, String memberUniqueId, boolean couponUsed, LocalDateTime issuedDate, LocalDateTime expiryDate, Integer discountAmount) {
        this.ownedCouponId = ownedCouponId;
        this.couponId = couponId;
        this.memberUniqueId = memberUniqueId;
        this.couponUsed = couponUsed;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.discountAmount = discountAmount;
    }

}
