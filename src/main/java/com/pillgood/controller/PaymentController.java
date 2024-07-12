package com.pillgood.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillgood.dto.BillingAuthRequest;
import com.pillgood.dto.BillingAuthResponse;
import com.pillgood.dto.BillingPaymentRequest;
import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.entity.Billing;
import com.pillgood.repository.BillingRepository;
import com.pillgood.repository.MemberRepository;
import com.pillgood.service.PaymentService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberRepository memberRepository;
    private final String clientKey;

    public PaymentController(PaymentService paymentService, MemberRepository memberRepository, @Value("${toss.client.key}") String clientKey) {
        this.paymentService = paymentService;
        this.memberRepository = memberRepository;
        this.clientKey = clientKey;
    }

    @GetMapping("/client-key")
    public ResponseEntity<String> getClientKey() {
        return ResponseEntity.ok("{\"clientKey\":\"" + clientKey + "\"}");
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

    @PostMapping("/issue-billing-key")
    public ResponseEntity<BillingAuthResponse> issueBillingKey(@RequestBody BillingAuthRequest request, HttpSession session) {
        try {
            BillingAuthResponse response = paymentService.issueBillingKey(request);
            if (response != null) {
                System.out.println("빌링키 발급 : " + response);
                // 세션에서 memberId 가져오기
                String memberId = (String) session.getAttribute("memberId");
                if (memberId != null) {
                    paymentService.saveBillingKey(response.getBillingKey(), memberId);
                } else {
                    System.out.println("memberId가 세션에 없습니다.");
                }
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/confirm-billing")
    public ResponseEntity<PaymentApproveResponse> confirmBilling(HttpSession session, @RequestBody BillingPaymentRequest request) {
        try {
            System.out.println("API 호출 확인 - confirmBilling");

            // 세션에서 memberUniqueId 가져오기
            String memberUniqueId = (String) session.getAttribute("memberId");
            if (memberUniqueId == null) {
                return ResponseEntity.status(400).body(null);
            }

            // memberUniqueId로 billingKey 가져오기
            Billing billing = paymentService.getBillingByMemberUniqueId(memberUniqueId);
            if (billing == null) {
                return ResponseEntity.status(404).body(null);
            }

            // billingKey로 결제 승인 요청
            PaymentApproveResponse response = paymentService.confirmBilling(request, billing.getBillingKey());
            System.out.println("응답 데이터: " + response);
            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).build();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/billing-key")
    public ResponseEntity<String> getBillingKey(HttpSession session) {
        String memberUniqueId = (String) session.getAttribute("memberId");
        if (memberUniqueId == null) {
            return ResponseEntity.status(400).body("세션에 memberUniqueId가 없습니다.");
        }

        Billing billing = paymentService.getBillingByMemberUniqueId(memberUniqueId);
        if (billing == null) {
            return ResponseEntity.ok(null); // billingKey가 없음
        } else {
            return ResponseEntity.ok(billing.getBillingKey()); // billingKey 반환
        }
    }
}
