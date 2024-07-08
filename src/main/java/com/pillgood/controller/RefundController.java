package com.pillgood.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pillgood.dto.RefundDto;
import com.pillgood.service.RefundService;

@RestController
public class RefundController {

    @Autowired
    private RefundService refundService;

    @GetMapping("/api/refunds/list")
    public List<RefundDto> getAllRefunds() {
        List<RefundDto> refunds = refundService.getAllRefunds();
        System.out.println("환불 목록 조회: " + refunds); // 로그 추가
        return refunds;
    }

    @GetMapping("/api/refunds/{id}")
    public ResponseEntity<RefundDto> getRefundById(@PathVariable int id) {
        System.out.println("환불 조회 요청: 환불 ID - " + id); // 로그 추가
        RefundDto refundDto = refundService.getRefundById(id);
        if (refundDto != null) {
            System.out.println("환불 조회 성공: " + refundDto); // 로그 추가
            return ResponseEntity.ok(refundDto);
        } else {
            System.out.println("환불 조회 실패: 환불 ID - " + id); // 로그 추가
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/refunds/create")
    public ResponseEntity<RefundDto> createRefund(@RequestBody RefundDto refundDto) {
        System.out.println("환불 생성 요청: " + refundDto); // 로그 추가
        refundDto.setRefundRequestDate(LocalDateTime.now()); // 현재 시간을 refund_request_date로 설정
        RefundDto createdRefund = refundService.createRefund(refundDto);
        System.out.println("환불 생성 성공: " + createdRefund); // 로그 추가
        return ResponseEntity.ok(createdRefund);
    }

    @PutMapping("/api/refunds/update/{id}")
    public ResponseEntity<RefundDto> updateRefund(@PathVariable int id, @RequestBody RefundDto refundDto) {
        System.out.println("환불 수정 요청: 환불 ID - " + id + ", 수정 내용 - " + refundDto); // 로그 추가
        RefundDto updatedRefund = refundService.updateRefund(id, refundDto);
        if (updatedRefund != null) {
            System.out.println("환불 수정 성공: " + updatedRefund); // 로그 추가
            return ResponseEntity.ok(updatedRefund);
        } else {
            System.out.println("환불 수정 실패: 환불 ID - " + id); // 로그 추가
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/refunds/delete/{id}")
    public ResponseEntity<Void> deleteRefund(@PathVariable int id) {
        System.out.println("환불 삭제 요청: 환불 ID - " + id); // 로그 추가
        refundService.deleteRefund(id);
        System.out.println("환불 삭제 성공: 환불 ID - " + id); // 로그 추가
        return ResponseEntity.noContent().build();
    }
}
