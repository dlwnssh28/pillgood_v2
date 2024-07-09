package com.pillgood.service;

import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.dto.PaymentRequest;
import com.pillgood.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public PaymentService(RestTemplate restTemplate, @Value("${toss.payments.secretKey}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public PaymentResponse requestPayment(PaymentRequest paymentRequest) {
        String url = "https://api.tosspayments.com/v1/payments";

        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
        PaymentResponse response = restTemplate.postForObject(url, requestEntity, PaymentResponse.class);

        // 결제 요청 성공 시 콘솔에 로그 출력
        if (response != null) {
            System.out.println("결제 요청 성공: " + response);
        } else {
            System.out.println("결제 요청 실패");
        }

        return response;
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
        } else {
            System.out.println("결제 승인 실패");
        }

        return response;
    }
}
