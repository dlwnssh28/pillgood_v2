package com.pillgood.controller;

import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.dto.PaymentRequest;
import com.pillgood.dto.PaymentResponse;
import com.pillgood.entity.Member;
import com.pillgood.service.PaymentService;
import com.pillgood.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

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

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody PaymentRequest paymentRequest, HttpSession session) {
        try {
            // 세션에서 memberId를 가져옴
            String memberId = (String) session.getAttribute("memberId");
            if (memberId == null) {
                return ResponseEntity.status(401).body(null);
            }

            // memberId로 회원 정보 조회
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return ResponseEntity.status(401).body(null);
            }

            Member member = memberOpt.get();

            // PaymentRequest에 회원 정보 추가
            paymentRequest.setCustomerName(member.getName());
            paymentRequest.setEmail(member.getEmail());
            paymentRequest.setPhoneNumber(member.getPhoneNumber());

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
