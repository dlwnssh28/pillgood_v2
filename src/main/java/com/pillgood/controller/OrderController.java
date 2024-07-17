package com.pillgood.controller;

import com.pillgood.dto.OrderDto;
import com.pillgood.dto.OrderItemDto;
import com.pillgood.service.OrderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/api/orders/{orderNo}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String orderNo) {
        OrderDto orderDto = orderService.getOrderById(orderNo);
        if (orderDto != null) {
            return ResponseEntity.ok(orderDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/orders/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto, HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        orderDto.setMemberUniqueId(memberId);

        // 주문 상품 목록 가져오기
        List<OrderItemDto> orderItems = (List<OrderItemDto>) session.getAttribute("orderItems");

        OrderDto createdOrder = orderService.createOrder(orderDto, orderItems);

        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
    
    @PostMapping("/api/orders/prepare")
    public ResponseEntity<String> prepareOrder(@RequestBody List<OrderItemDto> orderItems, HttpSession session) {
        session.setAttribute("orderItems", orderItems);
        System.out.println("주문 상품 정보 POST : " + orderItems);
        return new ResponseEntity<>("Order details set in session", HttpStatus.OK);
    }

    @GetMapping("/api/orders/prepare")
    public ResponseEntity<?> getPreparedOrder(HttpSession session) {
        List<OrderItemDto> orderItems = (List<OrderItemDto>) session.getAttribute("orderItems");
        System.out.println("주문 상품 정보 GET : " + orderItems);
        if (orderItems == null) {
            return new ResponseEntity<>("No order details in session", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(orderItems);
    }

    @PutMapping("/api/orders/update/{orderNo}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable String orderNo, @RequestBody OrderDto orderDto) {
        OrderDto updatedOrder = orderService.updateOrder(orderNo, orderDto);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/api/orders/cancel/{orderNo}")
    public ResponseEntity<Void> cancelOrder(HttpSession session, @PathVariable String orderNo) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        // 주문 취소 로직 추가
        OrderDto order = orderService.getOrderById(orderNo);
        if (order != null) {
            // 주문을 취소합니다.
            orderService.cancelOrder(orderNo);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/admin/orders/delete/{orderNo}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderNo) {
        orderService.deleteOrder(orderNo);
        return ResponseEntity.noContent().build();
    }

    // 사용자 ID 기반 주문 내역 조회
    @GetMapping("/api/orders/member")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<OrderDto> orders = orderService.getOrdersByUserId(memberId);
        if (orders != null && !orders.isEmpty()) {
            return ResponseEntity.ok(orders);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @PutMapping("/api/orders/update-status/{orderNo}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable String orderNo, @RequestBody Map<String, String> status) {
        String orderStatus = status.get("status");
        OrderDto updatedOrder = orderService.updateOrderStatus(orderNo, orderStatus);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
