package com.pillgood.service;

import com.pillgood.dto.OrderDetailDto;
import com.pillgood.entity.OrderDetail;
import com.pillgood.entity.Order;
import com.pillgood.entity.Product;
import com.pillgood.repository.OrderDetailRepository;
import com.pillgood.repository.OrderRepository;
import com.pillgood.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<OrderDetailDto> getAllOrderDetails() {
        return orderDetailRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailDto getOrderDetailById(int orderDetailNo) {
        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(orderDetailNo);
        return orderDetailOpt.map(this::convertToDto).orElse(null);
    }

    @Override
    public OrderDetailDto createOrderDetail(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetailEntity = convertToEntity(orderDetailDto);
        orderDetailRepository.save(orderDetailEntity);
        return convertToDto(orderDetailEntity);
    }

    @Override
    public OrderDetailDto updateOrderDetail(int orderDetailNo, OrderDetailDto orderDetailDto) {
        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(orderDetailNo);
        if (orderDetailOpt.isPresent()) {
            OrderDetail orderDetailEntity = orderDetailOpt.get();
            updateEntityFromDto(orderDetailEntity, orderDetailDto);
            orderDetailRepository.save(orderDetailEntity);
            return convertToDto(orderDetailEntity);
        }
        return null;
    }

    @Override
    public void deleteOrderDetail(int orderDetailNo) {
        orderDetailRepository.deleteById(orderDetailNo);
    }

    private OrderDetailDto convertToDto(OrderDetail orderDetailEntity) {
        return new OrderDetailDto(
                orderDetailEntity.getOrderDetailNo(),
                orderDetailEntity.getOrder().getOrderNo(), // 수정된 부분
                orderDetailEntity.getProduct().getProductId(), // 수정된 부분
                orderDetailEntity.getQuantity(),
                orderDetailEntity.getAmount()
        );
    }

    private OrderDetail convertToEntity(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailNo(orderDetailDto.getOrderDetailNo());

        // Order 객체를 설정
        Order order = orderRepository.findById(orderDetailDto.getOrderNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        orderDetail.setOrder(order);

        // Product 객체를 설정
        Product product = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        orderDetail.setProduct(product);

        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setAmount(orderDetailDto.getAmount());
        return orderDetail;
    }

    private void updateEntityFromDto(OrderDetail orderDetailEntity, OrderDetailDto orderDetailDto) {
        // Order 객체를 업데이트
        Order order = orderRepository.findById(orderDetailDto.getOrderNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        orderDetailEntity.setOrder(order);

        // Product 객체를 업데이트
        Product product = productRepository.findById(orderDetailDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        orderDetailEntity.setProduct(product);

        orderDetailEntity.setQuantity(orderDetailDto.getQuantity());
        orderDetailEntity.setAmount(orderDetailDto.getAmount());
    }

    @Override
    public List<OrderDetailDto> getOrderDetailsByOrderNo(String orderNo) {
        return orderDetailRepository.findByOrderOrderNo(orderNo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
