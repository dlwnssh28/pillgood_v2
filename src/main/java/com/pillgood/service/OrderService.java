package com.pillgood.service;

import com.pillgood.dto.OrderDto;
import com.pillgood.dto.OrderItemDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(String orderNo);
    OrderDto createOrder(OrderDto orderDto, List<OrderItemDto> orderItems);
    OrderDto updateOrder(String orderNo, OrderDto orderDto);
    void deleteOrder(String orderNo);
    List<OrderDto> getOrdersByUserId(String memberId); // 메서드 추가
    OrderDto updateOrderStatus(String orderNo, String status);
}
