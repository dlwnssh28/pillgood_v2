package com.pillgood.service;

import java.util.List;
import com.pillgood.dto.RefundDto;

public interface RefundService {
    List<RefundDto> getAllRefunds();
    RefundDto getRefundById(int refundId);
    RefundDto createRefund(RefundDto refundDto);
    RefundDto updateRefund(int refundId, RefundDto refundDto);
    void deleteRefund(int refundId);
    List<RefundDto> getRefundsByOrderNo(String orderNo); // 메소드 수정
}
