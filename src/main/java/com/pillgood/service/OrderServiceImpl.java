package com.pillgood.service;

import com.pillgood.dto.OrderDto;
import com.pillgood.dto.OrderItemDto;
import com.pillgood.entity.Order;
import com.pillgood.entity.OrderDetail;
import com.pillgood.entity.Product;
import com.pillgood.repository.OrderDetailRepository;
import com.pillgood.repository.OrderRepository;
import com.pillgood.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OwnedcouponService ownedcouponService;

    @Autowired
    private PointService pointService;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(String orderNo) {
        Optional<Order> orderOpt = orderRepository.findById(orderNo);
        return orderOpt.map(this::convertToDto).orElse(null);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderDto orderDto, List<OrderItemDto> orderItems) {
        Order orderEntity = convertToEntity(orderDto);

        // 기본 키 수동 설정
        String orderNo = generateOrderNo(); // 고유한 주문 번호 생성 로직을 여기에 추가하세요.
        orderEntity.setOrderNo(orderNo);
        orderEntity.setOrderStatus("주문완료");
        orderEntity.setOrderDate(LocalDateTime.now());
        orderRepository.save(orderEntity);

        // OrderDetails 저장
        for (OrderItemDto item : orderItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(orderEntity);

            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isPresent()) {
                orderDetail.setProduct(productOpt.get());
            } else {
                throw new IllegalArgumentException("Product not found: " + item.getProductId());
            }

            orderDetail.setQuantity(item.getProductQuantity());
            orderDetail.setAmount(item.getPrice());
            orderDetailRepository.save(orderDetail);
        }

        // 쿠폰이 null일 경우 null로 설정
        if (orderDto.getOwnedCouponId() != null) {
            orderEntity.setOwnedCouponId(orderDto.getOwnedCouponId());
            ownedcouponService.markCouponAsUsed(orderDto.getOwnedCouponId());
        } else {
            orderEntity.setOwnedCouponId(null); // 쿠폰이 없을 경우 null로 설정
        }

        // 포인트 사용
        if (orderDto.getPointsToUse() != null && orderDto.getPointsToUse() > 0) {
            pointService.usePoints(orderEntity.getMemberUniqueId(), orderDto.getPointsToUse(), orderEntity.getOrderNo());
        }

        return convertToDto(orderEntity);
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo) {
        // 주문 디테일 삭제
        orderDetailRepository.deleteByOrderOrderNo(orderNo);

        // 주문 은 주문취소상태로 변경
        Order order = orderRepository.findById(orderNo)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("주문취소");
        orderRepository.save(order);

        // 포인트 반환
        if (order.getPointsToUse() != null && order.getPointsToUse() > 0) {
            pointService.refundPoints(order.getMemberUniqueId(), order.getPointsToUse(), orderNo);
        }
    }

    @Override
    public OrderDto updateOrder(String orderNo, OrderDto orderDto) {
        Optional<Order> orderOpt = orderRepository.findById(orderNo);
        if (orderOpt.isPresent()) {
            Order orderEntity = orderOpt.get();
            updateEntityFromDto(orderEntity, orderDto);
            orderRepository.save(orderEntity);
            return convertToDto(orderEntity);
        }
        return null;
    }

    @Override
    public void deleteOrder(String orderNo) {
        orderRepository.deleteById(orderNo);
    }

    private OrderDto convertToDto(Order orderEntity) {
        OrderDto orderDto = new OrderDto(
                orderEntity.getOrderNo(),
                orderEntity.getTotalAmount(),
                orderEntity.getOrderRequest(),
                orderEntity.getOrderDate(),
                orderEntity.getRecipient(),
                orderEntity.getPostalCode(),
                orderEntity.getAddress(),
                orderEntity.getDetailedAddress(),
                orderEntity.getPhoneNumber(),
                orderEntity.getMemberUniqueId(),
                orderEntity.getOwnedCouponId(),
                orderEntity.getOrderStatus(),
                orderEntity.isSubscriptionStatus(),
                orderEntity.getPointsToUse() // 추가된 부분
        );
        return orderDto;
    }

    private Order convertToEntity(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderNo(orderDto.getOrderNo());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setOrderRequest(orderDto.getOrderRequest());
        order.setOrderDate(orderDto.getOrderDate());
        order.setRecipient(orderDto.getRecipient());
        order.setPostalCode(orderDto.getPostalCode());
        order.setAddress(orderDto.getAddress());
        order.setDetailedAddress(orderDto.getDetailedAddress());
        order.setPhoneNumber(orderDto.getPhoneNumber());
        order.setMemberUniqueId(orderDto.getMemberUniqueId());
        order.setOwnedCouponId(orderDto.getOwnedCouponId());
        order.setOrderStatus(orderDto.getOrderStatus());
        order.setSubscriptionStatus(orderDto.isSubscriptionStatus());
        order.setPointsToUse(orderDto.getPointsToUse()); // 추가된 부분
        return order;
    }

    private void updateEntityFromDto(Order orderEntity, OrderDto orderDto) {
        orderEntity.setTotalAmount(orderDto.getTotalAmount());
        orderEntity.setOrderRequest(orderDto.getOrderRequest());
        orderEntity.setOrderDate(orderDto.getOrderDate());
        orderEntity.setRecipient(orderDto.getRecipient());
        orderEntity.setPostalCode(orderDto.getPostalCode());
        orderEntity.setAddress(orderDto.getAddress());
        orderEntity.setDetailedAddress(orderDto.getDetailedAddress());
        orderEntity.setPhoneNumber(orderDto.getPhoneNumber());
        orderEntity.setMemberUniqueId(orderDto.getMemberUniqueId());
        orderEntity.setOwnedCouponId(orderDto.getOwnedCouponId());
        orderEntity.setOrderStatus(orderDto.getOrderStatus());
        orderEntity.setSubscriptionStatus(orderDto.isSubscriptionStatus());
        orderEntity.setPointsToUse(orderDto.getPointsToUse()); // 추가된 부분
    }

    @Override
    public List<OrderDto> getOrdersByUserId(String memberId) {
        List<Order> orders = orderRepository.findByMemberUniqueId(memberId);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findByOrderOrderNo(orderId);
    }

    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = generateRandomAlphaNumeric(6);
        return datePart + "-" + randomPart;
    }

    private String generateRandomAlphaNumeric(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    @Override
    @Transactional
    public void updateOrderStatusToPaid(String orderNo) {
        Optional<Order> orderOpt = orderRepository.findByOrderNo(orderNo);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setOrderStatus("결제완료");
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Order not found: " + orderNo);
        }
    }

    public OrderDto updateOrderStatus(String orderNo, String status) {
        Order order = orderRepository.findById(orderNo).orElse(null);
        if (order != null) {
            order.setOrderStatus(status);
            orderRepository.save(order);
            return new OrderDto(order);
        }
        return null;
    }
}
