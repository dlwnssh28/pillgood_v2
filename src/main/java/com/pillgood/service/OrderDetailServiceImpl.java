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
                orderDetailEntity.getOrder().getOrderNo(),
                orderDetailEntity.getProduct().getProductId(),
                orderDetailEntity.getQuantity(),
                orderDetailEntity.getAmount()
        );
    }

    private OrderDetail convertToEntity(OrderDetailDto orderDetailDto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailNo(orderDetailDto.getOrderDetailNo());

        Optional<Order> orderOpt = orderRepository.findById(orderDetailDto.getOrderNo());
        orderOpt.ifPresent(order -> orderDetail.setOrder(order));

        Optional<Product> productOpt = productRepository.findById(orderDetailDto.getProductId());
        productOpt.ifPresent(product -> orderDetail.setProduct(product));

        orderDetail.setQuantity(orderDetailDto.getQuantity());
        orderDetail.setAmount(orderDetailDto.getAmount());
        return orderDetail;
    }

    private void updateEntityFromDto(OrderDetail orderDetailEntity, OrderDetailDto orderDetailDto) {
        Optional<Order> orderOpt = orderRepository.findById(orderDetailDto.getOrderNo());
        orderOpt.ifPresent(order -> orderDetailEntity.setOrder(order));

        Optional<Product> productOpt = productRepository.findById(orderDetailDto.getProductId());
        productOpt.ifPresent(product -> orderDetailEntity.setProduct(product));

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
