package com.pillgood.controller;

import com.pillgood.dto.OrderDto;
import com.pillgood.service.OrderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

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
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
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
}
