package com.pillgood.controller;

import com.pillgood.dto.CouponDto;
import com.pillgood.dto.OwnedcouponDto;
import com.pillgood.service.OwnedcouponService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OwnedcouponController {

    private final OwnedcouponService ownedcouponService;

    @GetMapping("/admin/ownedcoupons/list")
    public ResponseEntity<List<OwnedcouponDto>> getAllOwnedCoupons() {
        List<OwnedcouponDto> ownedcoupons = ownedcouponService.getAllOwnedCoupons();
        return ResponseEntity.ok(ownedcoupons);
    }

    //접속중인 회원의 보유쿠폰 가져오기
    @GetMapping("/api/ownedcoupons/findbyid")
    public ResponseEntity<?> getOwnedCouponsFindByMemberId(HttpSession session) {
        String memberId = (String) session.getAttribute("memberId");
        
        if (memberId == null) {
            return new ResponseEntity<>("세션에 memberId가 없습니다.", HttpStatus.UNAUTHORIZED);
        }
        
        System.out.println(memberId + ": 쿠폰 조회");
        List<OwnedcouponDto> ownedcoupons = ownedcouponService.getOwnedCouponByMemberId(memberId);
        
        if (ownedcoupons.isEmpty()) {
            return new ResponseEntity<>("보유 쿠폰이 없습니다.", HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(ownedcoupons, HttpStatus.OK);
    }

    @GetMapping("/api/ownedcoupons/{ownedCouponId}")
    public ResponseEntity<OwnedcouponDto> getOwnedCouponById(@PathVariable Integer ownedCouponId) {
        return ownedcouponService.getOwnedCouponById(ownedCouponId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/ownedcoupons/create")
    public ResponseEntity<OwnedcouponDto> createOwnedCoupon(@RequestBody OwnedcouponDto ownedcouponDto) {
        OwnedcouponDto createdOwnedCoupon = ownedcouponService.createOwnedCoupon(ownedcouponDto);
        return ResponseEntity.ok(createdOwnedCoupon);
    }

    @PutMapping("/api/ownedcoupons/update/{ownedCouponId}")
    public ResponseEntity<OwnedcouponDto> updateOwnedCoupon(@PathVariable Integer ownedCouponId, @RequestBody OwnedcouponDto ownedcouponDto) {
        return ownedcouponService.updateOwnedCoupon(ownedCouponId, ownedcouponDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/ownedcoupons/delete/{ownedCouponId}")
    public ResponseEntity<Void> deleteOwnedCoupon(@PathVariable Integer ownedCouponId) {
        if (ownedcouponService.deleteOwnedCoupon(ownedCouponId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
