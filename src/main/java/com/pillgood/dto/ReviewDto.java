package com.pillgood.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Integer reviewId;
    private String memberUniqueId;
    private Integer orderDetailNo;
    private LocalDateTime reviewDate;
    private String reviewContent;
    private Integer rating;
    private String reviewImage;
    private boolean couponIssued;
    private String memberName; // 작성자 이름 추가

    // 필요한 생성자 추가
    public ReviewDto(Integer reviewId, String memberUniqueId, Integer orderDetailNo, LocalDateTime reviewDate, String reviewContent, Integer rating, String reviewImage, boolean couponIssued) {
        this.reviewId = reviewId;
        this.memberUniqueId = memberUniqueId;
        this.orderDetailNo = orderDetailNo;
        this.reviewDate = reviewDate;
        this.reviewContent = reviewContent;
        this.rating = rating;
        this.reviewImage = reviewImage;
        this.couponIssued = couponIssued;
    }
}
