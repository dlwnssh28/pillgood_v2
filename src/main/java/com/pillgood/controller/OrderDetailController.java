package com.pillgood.controller;

import com.pillgood.dto.OrderDetailDto;
import com.pillgood.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/api/order-details/list")
    public List<OrderDetailDto> getAllOrderDetails() {
        return orderDetailService.getAllOrderDetails();
    }

    @GetMapping("/api/order-details/{orderDetailNo}")
    public ResponseEntity<OrderDetailDto> getOrderDetailById(@PathVariable int orderDetailNo) {
        OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailById(orderDetailNo);
        if (orderDetailDto != null) {
            return ResponseEntity.ok(orderDetailDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/order-details/create")
    public ResponseEntity<OrderDetailDto> createOrderDetail(@RequestBody OrderDetailDto orderDetailDto) {
        OrderDetailDto createdOrderDetail = orderDetailService.createOrderDetail(orderDetailDto);
        return ResponseEntity.ok(createdOrderDetail);
    }

    @PutMapping("/api/order-details/update/{orderDetailNo}")
    public ResponseEntity<OrderDetailDto> updateOrderDetail(@PathVariable int orderDetailNo, @RequestBody OrderDetailDto orderDetailDto) {
        OrderDetailDto updatedOrderDetail = orderDetailService.updateOrderDetail(orderDetailNo, orderDetailDto);
        if (updatedOrderDetail != null) {
            return ResponseEntity.ok(updatedOrderDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/admin/order-details/delete/{orderDetailNo}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable int orderDetailNo) {
        orderDetailService.deleteOrderDetail(orderDetailNo);
        return ResponseEntity.noContent().build();
    }
}
