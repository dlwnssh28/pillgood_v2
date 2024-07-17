package com.pillgood.service;

import java.util.List;

import com.pillgood.dto.PointDto;

public interface PointService {
    PointDto createPoint(PointDto pointDto);
    List<PointDto> getPointsByMemberUniqueId(String memberUniqueId);
    Integer getTotalPointsByMemberUniqueId(String memberUniqueId);
	void usePoints(String memberUniqueId, Integer pointsToUse, String referenceId);
	void refundPoints(String memberUniqueId, Integer pointsToRefund, String referenceId);
}
