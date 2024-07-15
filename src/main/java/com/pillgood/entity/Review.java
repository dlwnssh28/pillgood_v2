package com.pillgood.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "member_unique_id", length = 36, nullable = false)
    private String memberUniqueId;

    @ManyToOne
    @JoinColumn(name = "order_detail_no", nullable = false)
    private OrderDetail orderDetail;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @Column(name = "review_content", columnDefinition = "TEXT", nullable = false)
    private String reviewContent;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_image", length = 255)
    private String reviewImage;

    @Column(name = "coupon_issued", nullable = false)
    private boolean couponIssued;

    public Review(Integer reviewId, String memberUniqueId, OrderDetail orderDetail, LocalDateTime reviewDate, String reviewContent, Integer rating, String reviewImage) {
        this.reviewId = reviewId;
        this.memberUniqueId = memberUniqueId;
        this.orderDetail = orderDetail;
        this.reviewDate = reviewDate;
        this.reviewContent = reviewContent;
        this.rating = rating;
        this.reviewImage = reviewImage;
        this.couponIssued = false; // 기본 값 설정
    }
}
