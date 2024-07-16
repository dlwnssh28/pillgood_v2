package com.pillgood.service;

import com.pillgood.dto.OrderDto;
import com.pillgood.dto.OrderItemDto;
import com.pillgood.entity.OrderDetail;

import java.util.List;

public interface OrderService {
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(String orderNo);
    OrderDto createOrder(OrderDto orderDto, List<OrderItemDto> orderItems);
    OrderDto updateOrder(String orderNo, OrderDto orderDto);
    void deleteOrder(String orderNo);
    List<OrderDto> getOrdersByUserId(String memberId);
    void cancelOrder(String orderNo);
    void updateOrderStatusToPaid(String orderNo);
    List<OrderDetail> getOrderDetailsByOrderId(String orderId);
    OrderDto updateOrderStatus(String orderNo, String status);
    
}
