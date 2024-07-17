package com.pillgood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillgood.dto.PointDetailDto;
import com.pillgood.entity.PointDetail;
import com.pillgood.repository.PointDetailRepository;
import com.pillgood.service.PointDetailService;

@Service
public class PointDetailServiceImpl implements PointDetailService {

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Override
    public PointDetailDto createPointDetail(PointDetailDto pointDetailDto) {
        PointDetail pointDetail = new PointDetail();
        pointDetail.setPointDetailId(pointDetailDto.getPointDetailId());
        pointDetail.setMemberUniqueId(pointDetailDto.getMemberUniqueId());
        pointDetail.setPointStatusCode(pointDetailDto.getPointStatusCode());
        pointDetail.setPoints(pointDetailDto.getPoints());
        pointDetail.setDetailHistoryId(pointDetailDto.getDetailHistoryId());
        pointDetail.setPointId(pointDetailDto.getPointId());
        pointDetail.setTransactionDate(pointDetailDto.getTransactionDate());
        pointDetail.setExpiryDate(pointDetailDto.getExpiryDate());

        PointDetail savedPointDetail = pointDetailRepository.save(pointDetail);
        return convertToDTO(savedPointDetail);
    }

    private PointDetailDto convertToDTO(PointDetail pointDetail) {
        return new PointDetailDto(
                pointDetail.getPointDetailId(),
                pointDetail.getMemberUniqueId(),
                pointDetail.getPointStatusCode(),
                pointDetail.getPoints(),
                pointDetail.getDetailHistoryId(),
                pointDetail.getPointId(),
                pointDetail.getTransactionDate(),
                pointDetail.getExpiryDate()
        );
    }
}
