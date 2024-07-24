package com.pillgood.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.pillgood.dto.PaymentCancelRequest;
import com.pillgood.dto.PaymentCancelResponse;
import com.pillgood.entity.Billing;
import com.pillgood.entity.Payment;
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
    public ResponseEntity<BillingAuthResponse> issueBillingKey(@RequestBody BillingAuthRequest request) {
        try {
            BillingAuthResponse response = paymentService.issueBillingKey(request);
            if (response != null) {
                System.out.println("빌링키 발급 : " + response);
                
                String memberId = response.getCustomerKey();
                if (memberId != null) {
                    paymentService.saveBillingKey(response.getBillingKey(), memberId);
                } else {
                    System.out.println("memberId가 없습니다.");
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
    public ResponseEntity<PaymentApproveResponse> confirmBilling(@RequestBody BillingPaymentRequest request) {
        try {
            System.out.println("API 호출 확인 - confirmBilling");

            // 세션에서 memberUniqueId 가져오기
            String memberId = request.getCustomerKey();
            if (memberId == null) {
            	System.out.println("memberId가 없습니다");
                return ResponseEntity.status(400).body(null);
            }

            // memberUniqueId로 billingKey 가져오기
            Billing billing = paymentService.getBillingByMemberUniqueId(memberId);
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

    @PostMapping("/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@RequestBody PaymentCancelRequest cancelRequest) {
        try {
            PaymentCancelResponse cancelResponse = paymentService.cancelPayment(cancelRequest);
            System.out.println(cancelResponse);
            if (cancelResponse != null) {
                paymentService.updatePaymentStatus(cancelResponse);
                return ResponseEntity.ok(cancelResponse);
            } else {
                return ResponseEntity.status(500).build();
            }
        } catch (Exception e) {
            System.out.println("결제 취소 요청 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/payment-info/{orderNo}")
    public ResponseEntity<Payment> getPaymentInfo(@PathVariable String orderNo) {
    	System.out.println("getPaymentInfo 호출됨 - orderNo: " + orderNo); // 로그 추가
        Optional<Payment> paymentOpt = paymentService.getPaymentByOrderNo(orderNo);
        if (paymentOpt.isPresent()) {
            return ResponseEntity.ok(paymentOpt.get());
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
    
    @GetMapping("/billing-key/{memberUniqueId}")
    public ResponseEntity<String> getBillingKey(@PathVariable String memberUniqueId, HttpSession session) {
        String sessionMemberId = (String) session.getAttribute("memberId");
        if (sessionMemberId == null || !sessionMemberId.equals(memberUniqueId)) {
            return ResponseEntity.status(403).body("세션에 memberUniqueId가 없거나 일치하지 않습니다.");
        }

        Billing billing = paymentService.getBillingByMemberUniqueId(memberUniqueId);
        if (billing == null) {
            return ResponseEntity.ok(null); // billingKey가 없음
        } else {
            return ResponseEntity.ok(billing.getBillingKey()); // billingKey 반환
        }
    }
    
    @DeleteMapping("/delete-billing-key/{memberUniqueId}")
    public ResponseEntity<Void> deleteBillingKey(@PathVariable String memberId) {
        try {
            paymentService.deleteBillingKey(memberId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
