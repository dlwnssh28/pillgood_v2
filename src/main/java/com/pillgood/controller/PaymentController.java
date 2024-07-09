package com.pillgood.controller;

import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.dto.PaymentRequest;
import com.pillgood.dto.PaymentResponse;
import com.pillgood.service.PaymentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final String clientKey;

    public PaymentController(PaymentService paymentService, @Value("${toss.client.key}") String clientKey) {
        this.paymentService = paymentService;
        this.clientKey = clientKey;
    }

    @GetMapping("/client-key")
    public ResponseEntity<String> getClientKey() {
        return ResponseEntity.ok("{\"clientKey\":\"" + clientKey + "\"}");
    }

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse paymentResponse = paymentService.requestPayment(paymentRequest);
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            System.out.println("결제 요청 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<PaymentApproveResponse> approvePayment(@RequestBody PaymentApproveRequest approveRequest) {
        try {
            System.out.println("결제 승인 요청 수신: " + approveRequest); // 디버깅을 위해 로그 추가
            PaymentApproveResponse approveResponse = paymentService.approvePayment(approveRequest);
            // 결제 승인 성공 시 콘솔에 로그 출력
            if (approveResponse != null) {
                System.out.println("결제 승인 요청 성공: " + approveResponse);
            } else {
                System.out.println("결제 승인 요청 실패");
            }
            return ResponseEntity.ok(approveResponse);
        } catch (Exception e) {
            System.out.println("결제 승인 요청 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
