package com.pillgood.controller;

import com.pillgood.dto.CartDto;
import com.pillgood.dto.CouponDto;
import com.pillgood.service.CouponService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/api/coupons/list")
    public ResponseEntity<List<CouponDto>> getAllCoupons() {
        List<CouponDto> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/api/coupons/{couponId}")
    public ResponseEntity<CouponDto> getCouponById(@PathVariable Integer couponId) {
        return couponService.getCouponById(couponId) 
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/coupons/create")
    public ResponseEntity<CouponDto> createCoupon(@RequestBody CouponDto couponDto) {
        CouponDto createdCoupon = couponService.createCoupon(couponDto);
        return ResponseEntity.ok(createdCoupon);
    }

    @PutMapping("/admin/coupons/{couponId}")
    public ResponseEntity<CouponDto> updateCoupon(@PathVariable Integer couponId, @RequestBody CouponDto couponDto) {
        return couponService.updateCoupon(couponId, couponDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/coupons/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Integer couponId) {
        if (couponService.deleteCoupon(couponId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}