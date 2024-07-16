package com.pillgood.service;

import java.util.List;

import com.pillgood.dto.PointDto;

public interface PointService {
    PointDto createPoint(PointDto pointDto);
    void usePoints(String memberUniqueId, Integer pointsToUse);
    List<PointDto> getPointsByMemberUniqueId(String memberUniqueId);
    Integer getTotalPointsByMemberUniqueId(String memberUniqueId);
    void refundPoints(String memberUniqueId, Integer pointsToRefund);
}
