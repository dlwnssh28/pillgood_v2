package com.pillgood.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillgood.dto.RefundDto;
import com.pillgood.entity.Order;
import com.pillgood.entity.Refund;
import com.pillgood.repository.RefundRepository;
import com.pillgood.repository.OrderRepository;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<RefundDto> getAllRefunds() {
        return refundRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RefundDto getRefundById(int refundId) {
        Optional<Refund> refundOpt = refundRepository.findById(refundId);
        if (refundOpt.isPresent()) {
            Refund refund = refundOpt.get();
            return convertToDto(refund);
        }
        throw new NoSuchElementException("Refund not found for id: " + refundId);
    }

    @Override
    public RefundDto createRefund(RefundDto refundDto) {
        // 주문 번호로 주문을 조회하여 주문 금액을 가져옵니다.
        Optional<Order> orderOpt = orderRepository.findByOrderNo(refundDto.getOrderNo());
        if (!orderOpt.isPresent()) {
            throw new NoSuchElementException("Order not found for orderNo: " + refundDto.getOrderNo());
        }
        
        Order order = orderOpt.get();
        int orderTotalAmount = order.getTotalAmount();
        
        // 환불 엔티티 생성 및 주문 금액 설정
        Refund refundEntity = convertToEntity(refundDto);
        refundEntity.setTotalRefundAmount(orderTotalAmount); // 주문 금액을 환불 금액으로 설정
        
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        refundEntity.setRefundRequestDate(now);
        
        refundRepository.save(refundEntity);
        
        return convertToDto(refundEntity);
    }

    @Override
    public RefundDto updateRefund(int refundId, RefundDto refundDto) {
        Optional<Refund> refundOpt = refundRepository.findById(refundId);
        if (refundOpt.isPresent()) {
            Refund refundEntity = refundOpt.get();
            updateEntityFromDto(refundEntity, refundDto);
            refundRepository.save(refundEntity);
            return convertToDto(refundEntity);
        }
        return null;
    }

    @Override
    public void deleteRefund(int refundId) {
        refundRepository.deleteById(refundId);
    }

    @Override
    public List<RefundDto> getRefundsByOrderNo(String orderNo) {
        return refundRepository.findByOrderOrderNo(orderNo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RefundDto convertToDto(Refund refundEntity) {
        return new RefundDto(
                refundEntity.getRefundId(),
                refundEntity.getRefundRequestDate(),
                refundEntity.getRefundCompleteDate(),
                refundEntity.getTotalRefundAmount(),
                refundEntity.getRefundMethod(),
                refundEntity.getRefundBank(),
                refundEntity.getRefundStatus(),
                refundEntity.getOrder().getOrderNo()
        );
    }

    private Refund convertToEntity(RefundDto refundDto) {
        Refund refund = new Refund();
        refund.setRefundRequestDate(refundDto.getRefundRequestDate());
        refund.setRefundCompleteDate(refundDto.getRefundCompleteDate());
        refund.setTotalRefundAmount(refundDto.getTotalRefundAmount());
        refund.setRefundMethod(refundDto.getRefundMethod());
        refund.setRefundBank(refundDto.getRefundBank());
        refund.setRefundStatus(refundDto.getRefundStatus());
        
        Optional<Order> orderOpt = orderRepository.findByOrderNo(refundDto.getOrderNo());
        if (orderOpt.isPresent()) {
            refund.setOrder(orderOpt.get());
        }
        
        return refund;
    }

    private void updateEntityFromDto(Refund refundEntity, RefundDto refundDto) {
        refundEntity.setRefundRequestDate(refundDto.getRefundRequestDate());
        refundEntity.setRefundCompleteDate(refundDto.getRefundCompleteDate());
        refundEntity.setTotalRefundAmount(refundDto.getTotalRefundAmount());
        refundEntity.setRefundMethod(refundDto.getRefundMethod());
        refundEntity.setRefundBank(refundDto.getRefundBank());
        refundEntity.setRefundStatus(refundDto.getRefundStatus());
        
        Optional<Order> orderOpt = orderRepository.findByOrderNo(refundDto.getOrderNo());
        if (orderOpt.isPresent()) {
            refundEntity.setOrder(orderOpt.get());
        }
    }
}
