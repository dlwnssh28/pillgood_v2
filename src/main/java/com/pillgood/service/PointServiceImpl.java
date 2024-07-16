package com.pillgood.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
import com.pillgood.service.PointService;
import com.pillgood.service.PointDetailService;

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
        PointDetailDto pointDetailDto = new PointDetailDto();
        pointDetailDto.setMemberUniqueId(point.getMemberUniqueId());
        pointDetailDto.setPointStatusCode(point.getPointStatusCode());
        pointDetailDto.setPoints(point.getPoints());
        pointDetailDto.setDetailHistoryId(point.getPointId());
        pointDetailDto.setPointId(point.getPointId());
        pointDetailDto.setTransactionDate(point.getTransactionDate());

        pointDetailService.createPointDetail(pointDetailDto);

        return pointDto;
    }

    @Override
    public List<PointDto> getPointsByMemberUniqueId(String memberUniqueId) {
        List<Point> pointsList = pointRepository.findByMemberUniqueId(memberUniqueId);
        return pointsList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void usePoints(String memberUniqueId, Integer pointsToUse) {
        List<PointDetail> pointDetailsList = pointDetailRepository.findByMemberUniqueIdAndPointsGreaterThanOrderByTransactionDateAsc(memberUniqueId, 0);
        int remainingPoints = pointsToUse;

        // 포인트 사용 이벤트 추가
        Point pointEvent = new Point();
        pointEvent.setMemberUniqueId(memberUniqueId);
        pointEvent.setPointMasterId("ORDER");
        pointEvent.setPointStatusCode("PU");
        pointEvent.setPoints(-pointsToUse);
        pointEvent.setTransactionDate(LocalDateTime.now());
        pointEvent.setReferenceId(UUID.randomUUID().toString());

        pointEvent = pointRepository.save(pointEvent);

        for (PointDetail pointDetail : pointDetailsList) {
            if (remainingPoints <= 0) break;

            int pointsAvailable = pointDetail.getPoints();
            int pointsToDeduct = Math.min(pointsAvailable, remainingPoints);

            PointDetail deductionDetail = new PointDetail();
            deductionDetail.setMemberUniqueId(pointDetail.getMemberUniqueId());
            deductionDetail.setPointStatusCode("PU");
            deductionDetail.setPoints(-pointsToDeduct);
            deductionDetail.setDetailHistoryId(pointDetail.getPointDetailId());
            deductionDetail.setPoint(pointEvent); // 현재 사용 이벤트에 대한 point_id 참조
            deductionDetail.setTransactionDate(LocalDateTime.now());  // transactionDate 초기화

            pointDetailRepository.save(deductionDetail);

            remainingPoints -= pointsToDeduct;
        }

        if (remainingPoints > 0) {
            throw new RuntimeException("Insufficient points");
        }
    }

    @Override
    @Transactional
    public void refundPoints(String memberUniqueId, Integer pointsToRefund) {
        Point pointEvent = new Point();
        pointEvent.setMemberUniqueId(memberUniqueId);
        pointEvent.setPointMasterId("REFUND");
        pointEvent.setPointStatusCode("UC");
        pointEvent.setPoints(pointsToRefund);
        pointEvent.setTransactionDate(LocalDateTime.now());
        pointEvent.setReferenceId(UUID.randomUUID().toString());

        pointRepository.save(pointEvent);

        PointDetailDto pointDetailDto = new PointDetailDto();
        pointDetailDto.setMemberUniqueId(memberUniqueId);
        pointDetailDto.setPointStatusCode("UC");
        pointDetailDto.setPoints(pointsToRefund);
        pointDetailDto.setDetailHistoryId(pointEvent.getPointId());
        pointDetailDto.setPointId(pointEvent.getPointId());
        pointDetailDto.setTransactionDate(LocalDateTime.now());

        pointDetailService.createPointDetail(pointDetailDto);
    }

    @Override
    public Integer getTotalPointsByMemberUniqueId(String memberUniqueId) {
        return pointRepository.findTotalPointsByMemberUniqueId(memberUniqueId);
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
