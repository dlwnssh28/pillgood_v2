package com.pillgood.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pillgood.dto.PointDto;
import com.pillgood.dto.PointDetailDto;
import com.pillgood.entity.Point;
import com.pillgood.entity.PointDetail;
import com.pillgood.repository.PointRepository;
import com.pillgood.repository.PointDetailRepository;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private PointDetailService pointDetailService;

    @Override
    @Transactional
    public PointDto createPoint(PointDto pointDto) {
        Point point = new Point();
        point.setMemberUniqueId(pointDto.getMemberUniqueId());
        point.setPointMasterId(pointDto.getPointMasterId());
        point.setPointStatusCode(pointDto.getPointStatusCode());
        point.setPoints(pointDto.getPoints());
        point.setTransactionDate(pointDto.getTransactionDate());
        point.setExpiryDate(pointDto.getExpiryDate());
        point.setReferenceId(pointDto.getReferenceId());

        point = pointRepository.save(point);  // Point를 저장
        pointDto.setPointId(point.getPointId());

        // 포인트 적립 상세 생성
        int pointDetailId = generateNewPointDetailId();
        PointDetailDto pointDetailDto = new PointDetailDto();
        pointDetailDto.setPointDetailId(pointDetailId); // Set the generated ID
        pointDetailDto.setMemberUniqueId(point.getMemberUniqueId());
        pointDetailDto.setPointStatusCode(point.getPointStatusCode());
        pointDetailDto.setPoints(point.getPoints());
        pointDetailDto.setDetailHistoryId(pointDetailId); // Use the generated ID
        pointDetailDto.setPointId(point.getPointId());
        pointDetailDto.setTransactionDate(point.getTransactionDate());
        pointDetailDto.setExpiryDate(point.getExpiryDate()); // 만료일 설정

        pointDetailService.createPointDetail(pointDetailDto);

        return pointDto;
    }

    private int generateNewPointDetailId() {
        Integer maxId = pointDetailRepository.findMaxPointDetailId();
        return (maxId == null ? 0 : maxId) + 1;
    }

    @Override
    public List<PointDto> getPointsByMemberUniqueId(String memberUniqueId) {
        List<Point> pointsList = pointRepository.findByMemberUniqueId(memberUniqueId);
        return pointsList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void usePoints(String memberUniqueId, Integer pointsToUse, String referenceId) {
        List<PointDetail> pointDetailsList = pointDetailRepository.findByMemberUniqueIdAndPointsGreaterThanOrderByTransactionDateAsc(memberUniqueId, 0);
        int remainingPoints = pointsToUse;

        // 포인트 사용 이벤트 추가
        Point pointEvent = new Point();
        pointEvent.setMemberUniqueId(memberUniqueId);
        pointEvent.setPointMasterId("ORDER");
        pointEvent.setPointStatusCode("PU");
        pointEvent.setPoints(-pointsToUse);
        pointEvent.setTransactionDate(LocalDateTime.now());
        pointEvent.setReferenceId(referenceId);

        pointEvent = pointRepository.save(pointEvent);  // Point 이벤트를 저장
        int pointEventId = pointEvent.getPointId();

        for (PointDetail pointDetail : pointDetailsList) {
            if (remainingPoints <= 0) break;

            int pointsAvailable = pointDetail.getPoints();
            int pointsToDeduct = Math.min(pointsAvailable, remainingPoints);

            PointDetail deductionDetail = new PointDetail();
            deductionDetail.setPointDetailId(generateNewPointDetailId()); 
            deductionDetail.setMemberUniqueId(pointDetail.getMemberUniqueId());
            deductionDetail.setPointStatusCode("PU");
            deductionDetail.setPoints(-pointsToDeduct);
            deductionDetail.setDetailHistoryId(pointDetail.getPointDetailId());
            deductionDetail.setPointId(pointEventId);  // 현재 사용 이벤트에 대한 point_id 참조
            deductionDetail.setTransactionDate(LocalDateTime.now());
            deductionDetail.setExpiryDate(pointDetail.getExpiryDate());

            pointDetailRepository.save(deductionDetail);

            remainingPoints -= pointsToDeduct;
        }

        if (remainingPoints > 0) {
            throw new RuntimeException("Insufficient points");
        }
    }

    @Override
    @Transactional
    public void refundPoints(String memberUniqueId, Integer pointsToRefund, String referenceId) {
        List<Point> usedPoints = pointRepository.findByMemberUniqueIdAndPointStatusCodeAndReferenceId(memberUniqueId, "PU", referenceId);
        int totalRefundedPoints = 0;

        Point pointEvent = new Point();
        pointEvent.setMemberUniqueId(memberUniqueId);
        pointEvent.setPointMasterId("REFUND");
        pointEvent.setPointStatusCode("UC");
        pointEvent.setPoints(pointsToRefund);
        pointEvent.setTransactionDate(LocalDateTime.now());
        pointEvent.setReferenceId(referenceId);

        pointEvent = pointRepository.save(pointEvent);  // Point를 저장

        for (Point usedPoint : usedPoints) {
            List<PointDetail> usedPointsDetails = pointDetailRepository.findByMemberUniqueIdAndPointStatusCodeAndPointId(memberUniqueId, "PU", usedPoint.getPointId());

            for (PointDetail usedPointDetail : usedPointsDetails) {
                if (totalRefundedPoints >= pointsToRefund) {
                    break;
                }

                int pointsToReturn = Math.min(pointsToRefund - totalRefundedPoints, -usedPointDetail.getPoints());

                PointDetail returnDetail = new PointDetail();
                returnDetail.setPointDetailId(generateNewPointDetailId()); // Generate new ID
                returnDetail.setMemberUniqueId(memberUniqueId);
                returnDetail.setPointStatusCode("UC");
                returnDetail.setPoints(pointsToReturn);
                returnDetail.setDetailHistoryId(usedPointDetail.getPointDetailId());
                returnDetail.setPointId(pointEvent.getPointId());
                returnDetail.setTransactionDate(LocalDateTime.now());
                returnDetail.setExpiryDate(usedPointDetail.getExpiryDate());

                pointDetailRepository.save(returnDetail);

                totalRefundedPoints += pointsToReturn;
            }

            if (totalRefundedPoints >= pointsToRefund) {
                break;
            }
        }

        if (totalRefundedPoints < pointsToRefund) {
            throw new RuntimeException("Not enough points to refund");
        }
    }

    @Override
    public Integer getTotalPointsByMemberUniqueId(String memberUniqueId) {
        return pointRepository.findTotalPointsByMemberUniqueId(memberUniqueId);
    }

    @Override
    public List<PointDto> getPointsByMemberUniqueIdAndReferenceId(String memberUniqueId, String pointMasterId, String referenceId) {
        List<Point> points = pointRepository.findByMemberUniqueIdAndPointStatusCodeAndReferenceId(memberUniqueId, pointMasterId, referenceId);
        return points.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private PointDto convertToDTO(Point point) {
        PointDto pointDto = new PointDto();
        pointDto.setPointId(point.getPointId());
        pointDto.setMemberUniqueId(point.getMemberUniqueId());
        pointDto.setPointMasterId(point.getPointMasterId());
        pointDto.setPointStatusCode(point.getPointStatusCode());
        pointDto.setPoints(point.getPoints());
        pointDto.setTransactionDate(point.getTransactionDate());
        pointDto.setExpiryDate(point.getExpiryDate());
        pointDto.setReferenceId(point.getReferenceId());
        return pointDto;
    }
}
