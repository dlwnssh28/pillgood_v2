package com.pillgood.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillgood.dto.OrderDetailDto;
import com.pillgood.dto.SubscriptionDto;
import com.pillgood.entity.Payment;
import com.pillgood.entity.Subscription;
import com.pillgood.repository.OrderDetailRepository;
import com.pillgood.entity.OrderDetail;
import com.pillgood.repository.PaymentRepository;
import com.pillgood.repository.SubscriptionRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<SubscriptionDto> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto getSubscriptionById(int subscriptionId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        return subscriptionOpt.map(this::convertToDto).orElse(null);
    }

    @Override
    public List<SubscriptionDto> getSubscriptionsByMemberUniqueId(String memberUniqueId) {
        List<Subscription> subscriptions = subscriptionRepository.findByMemberUniqueId(memberUniqueId);
        return subscriptions.stream().map(subscription -> {
            SubscriptionDto dto = new SubscriptionDto(
                subscription.getSubscriptionId(),
                subscription.getMemberUniqueId(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getSubscriptionStatus(),
                subscription.getPaymentNo()
            );

            // Payments 테이블에서 orderNo 조회
            Payment payment = paymentRepository.findByPaymentNo(subscription.getPaymentNo()).orElse(null);
            if (payment != null) {
                // OrderDetail 테이블에서 주문 상세 정보 조회
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderNo(payment.getOrderNo());
                List<OrderDetailDto> orderDetailDtos = orderDetails.stream().map(orderDetail -> new OrderDetailDto(
                    orderDetail.getOrderDetailNo(),
                    orderDetail.getOrder().getOrderNo(),
                    orderDetail.getProduct().getProductId(),
                    orderDetail.getQuantity(),
                    orderDetail.getAmount()
                )).collect(Collectors.toList());
                dto.setOrderDetails(orderDetailDtos);
            }

            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public SubscriptionDto createSubscription(SubscriptionDto subscriptionDto) {
        Subscription subscriptionEntity = convertToEntity(subscriptionDto);
        subscriptionRepository.save(subscriptionEntity);
        return convertToDto(subscriptionEntity);
    }

    @Override
    public SubscriptionDto updateSubscription(int subscriptionId, SubscriptionDto subscriptionDto) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscriptionEntity = subscriptionOpt.get();
            updateEntityFromDto(subscriptionEntity, subscriptionDto);
            subscriptionRepository.save(subscriptionEntity);
            return convertToDto(subscriptionEntity);
        }
        return null;
    }

    @Override
    public void deleteSubscription(int subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }

    private SubscriptionDto convertToDto(Subscription subscriptionEntity) {
        return new SubscriptionDto(
                subscriptionEntity.getSubscriptionId(),
                subscriptionEntity.getMemberUniqueId(),
                subscriptionEntity.getStartDate(),
                subscriptionEntity.getEndDate(),
                subscriptionEntity.getSubscriptionStatus(),
                subscriptionEntity.getPaymentNo()
        );
    }

    private Subscription convertToEntity(SubscriptionDto subscriptionDto) {
        return new Subscription(
                subscriptionDto.getSubscriptionId(),
                subscriptionDto.getMemberUniqueId(),
                subscriptionDto.getStartDate(),
                subscriptionDto.getEndDate(),
                subscriptionDto.getSubscriptionStatus(),
                subscriptionDto.getPaymentNo()
        );
    }

    private void updateEntityFromDto(Subscription subscriptionEntity, SubscriptionDto subscriptionDto) {
        subscriptionEntity.setMemberUniqueId(subscriptionDto.getMemberUniqueId());
        subscriptionEntity.setStartDate(subscriptionDto.getStartDate());
        subscriptionEntity.setEndDate(subscriptionDto.getEndDate());
        subscriptionEntity.setSubscriptionStatus(subscriptionDto.getSubscriptionStatus());
        subscriptionEntity.setPaymentNo(subscriptionDto.getPaymentNo());
    }
}
