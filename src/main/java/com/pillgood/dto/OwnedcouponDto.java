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
    private Integer ownedCouponId;
    private Integer couponId;
    private String memberUniqueId;
    private boolean couponUsed;
    private LocalDateTime issuedDate;
    private LocalDateTime expiryDate;
//    private String couponName;
//    private Integer discountAmount;

    public OwnedcouponDto(Integer ownedCouponId, Integer couponId, String memberUniqueId, boolean couponUsed, LocalDateTime issuedDate, LocalDateTime expiryDate, String couponName, Integer discountAmount) {
        this.ownedCouponId = ownedCouponId;
        this.couponId = couponId;
        this.memberUniqueId = memberUniqueId;
        this.couponUsed = couponUsed;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
//        this.couponName = couponName;
//        this.discountAmount = discountAmount;
    }

    @Override
    public String toString() {
        return "OwnedcouponDto{" +
                "ownedCouponId=" + ownedCouponId +
                ", couponId=" + couponId +
                ", memberUniqueId='" + memberUniqueId + '\'' +
                ", couponUsed=" + couponUsed +
                ", issuedDate=" + issuedDate +
                ", expiryDate=" + expiryDate +
//                ", couponName='" + couponName + '\'' +
//                ", discountAmount=" + discountAmount +
                '}';
    }
}