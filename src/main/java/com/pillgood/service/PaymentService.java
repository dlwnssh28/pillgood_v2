package com.pillgood.service;

import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.dto.PaymentRequest;
import com.pillgood.dto.PaymentResponse;
import com.pillgood.entity.Payment;
import com.pillgood.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

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
            // 결제 내역 저장
            savePaymentDetails(approveRequest, response);
        } else {
            System.out.println("결제 승인 실패");
        }

        return response;
    }

    private void savePaymentDetails(PaymentApproveRequest approveRequest, PaymentApproveResponse approveResponse) {
        // 고유 결제 번호 생성
        String paymentNo = generatePaymentNo();

        Payment payment = new Payment();
        payment.setPaymentNo(paymentNo);
        payment.setOrderNo(approveRequest.getOrderId());
        payment.setPaymentKey(approveRequest.getPaymentKey());
        payment.setAmount(approveResponse.getAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setMemberUniqueId(approveRequest.getMemberUniqueId()); // 수정된 부분

        // 추가 정보를 설정할 수 있습니다.
        paymentRepository.save(payment);
    }

    private String generatePaymentNo() {
        String dateTimePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss"));
        String randomPart = generateRandomAlpha(4);
        return dateTimePart + "-" + randomPart;
    }

    private String generateRandomAlpha(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
