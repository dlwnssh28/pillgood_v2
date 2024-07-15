package com.pillgood.controller;

import com.pillgood.dto.ReviewDto;
import com.pillgood.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/reviews/list")
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        List<ReviewDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/reviews/create")
    public ResponseEntity<ReviewDto> createReview(HttpSession session, @RequestBody ReviewDto reviewDto) {
        try {
            String memberId = (String) session.getAttribute("memberId");
            System.out.println("Session memberId: " + memberId);  // 세션에 저장된 memberId 출력
            if (memberId == null) {
                System.out.println("Member ID not found in session. Returning FORBIDDEN.");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            reviewDto.setMemberUniqueId(memberId);
            System.out.println("Creating review with data: " + reviewDto);  // 리뷰 데이터 출력
            ReviewDto createdReview = reviewService.createReview(reviewDto);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/api/reviews/update/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Integer reviewId, @RequestBody ReviewDto reviewDto) {
        System.out.println("Update request received for reviewId: " + reviewId);
        System.out.println("ReviewDto: " + reviewDto);
        return reviewService.updateReview(reviewId, reviewDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/reviews/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        if (reviewService.deleteReview(reviewId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/reviews/update-coupon-issued")
    public ResponseEntity<Void> updateCouponIssued(@RequestParam int reviewId, @RequestParam boolean couponIssued) {
        reviewService.updateCouponIssued(reviewId, couponIssued);
        return ResponseEntity.ok().build();
    }
}
