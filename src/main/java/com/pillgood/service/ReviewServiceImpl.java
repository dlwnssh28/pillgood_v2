package com.pillgood.service;

import com.pillgood.dto.MemberDto;
import com.pillgood.dto.ReviewDto;
import com.pillgood.entity.OrderDetail;
import com.pillgood.entity.Review;
import com.pillgood.repository.OrderDetailRepository;
import com.pillgood.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberService memberService;

    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<ReviewDto> getReviewById(int reviewId) {
        return reviewRepository.findById(reviewId)
                .map(this::convertToDto);
    }

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        OrderDetail orderDetail = orderDetailRepository.findById(reviewDto.getOrderDetailNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid order detail ID"));

        reviewDto.setReviewDate(LocalDateTime.now());

        Review reviewEntity = convertToEntity(reviewDto, orderDetail);
        Review savedReview = reviewRepository.save(reviewEntity);
        return convertToDto(savedReview);
    }

    @Override
    public Optional<ReviewDto> updateReview(int reviewId, ReviewDto updatedReviewDto) {
        return reviewRepository.findById(reviewId)
                .map(review -> {
                    review.setReviewContent(updatedReviewDto.getReviewContent());
                    review.setRating(updatedReviewDto.getRating());
                    review.setReviewImage(updatedReviewDto.getReviewImage());
                    Review updatedReview = reviewRepository.save(review);
                    return convertToDto(updatedReview);
                });
    }

    @Override
    public boolean deleteReview(int reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }

    @Override
    public ReviewDto convertToDto(Review reviewEntity) {
        return new ReviewDto(
                reviewEntity.getReviewId(),
                reviewEntity.getMemberUniqueId(),
                reviewEntity.getOrderDetail().getOrderDetailNo(),
                reviewEntity.getReviewDate(),
                reviewEntity.getReviewContent(),
                reviewEntity.getRating(),
                reviewEntity.getReviewImage()
        );
    }

    @Override
    public Review convertToEntity(ReviewDto reviewDto, OrderDetail orderDetail) {
        return new Review(
                null, // reviewId는 자동으로 생성됨
                reviewDto.getMemberUniqueId(),
                orderDetail,
                reviewDto.getReviewDate(),
                reviewDto.getReviewContent(),
                reviewDto.getRating(),
                reviewDto.getReviewImage()
        );
    }
}
