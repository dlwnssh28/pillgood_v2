package com.pillgood.service;

import com.pillgood.dto.ReviewDto;
import com.pillgood.entity.Review;
import com.pillgood.entity.OrderDetail;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    Optional<ReviewDto> getReviewById(int reviewId);
    ReviewDto createReview(ReviewDto reviewDto);
    Optional<ReviewDto> updateReview(int reviewId, ReviewDto updatedReviewDto);
    boolean deleteReview(int reviewId);
    ReviewDto convertToDto(Review reviewEntity);
    Review convertToEntity(ReviewDto reviewDto, OrderDetail orderDetail);
    void updateCouponIssued(int reviewId, boolean couponIssued); // 새로운 메서드 추가
}
