package com.pillgood.service;

import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.entity.Payment;
import com.pillgood.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final PaymentRepository paymentRepository;

    public PaymentService(RestTemplate restTemplate, @Value("${toss.payments.secretKey}") String apiKey, PaymentRepository paymentRepository) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
    }

    public PaymentApproveResponse approvePayment(PaymentApproveRequest approveRequest) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        System.out.println("결제 승인 요청 전송: " + approveRequest); // 디버깅 로그
        System.out.println("Authorization Header: " + headers.getFirst("Authorization")); // 디버깅 로그

        HttpEntity<PaymentApproveRequest> requestEntity = new HttpEntity<>(approveRequest, headers);
        PaymentApproveResponse response = restTemplate.postForObject(url, requestEntity, PaymentApproveResponse.class);

        // 결제 승인 성공 시 콘솔에 로그 출력
        if (response != null) {
            System.out.println("결제 승인 성공: " + response);
            // 결제 내역 저장
            savePaymentDetails(approveRequest, response);
        } else {
            System.out.println("결제 승인 실패");
        }

        return response;
    }

    private void savePaymentDetails(PaymentApproveRequest approveRequest, PaymentApproveResponse approveResponse) {

        Payment payment = new Payment();
        payment.setPaymentNo(approveResponse.getPaymentKey());
        payment.setOrderNo(approveResponse.getOrderId());
        payment.setAmount(approveResponse.getTotalAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setMethod(approveResponse.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscriptionStatus(approveRequest.isSubscriptionStatus()); // 수정된 부분

        // 추가 정보를 설정할 수 있습니다.
        paymentRepository.save(payment);
    }
}
