package com.pillgood.service;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pillgood.dto.BillingAuthRequest;
import com.pillgood.dto.BillingAuthResponse;
import com.pillgood.dto.BillingPaymentRequest;
import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.entity.Billing;
import com.pillgood.entity.Payment;
import com.pillgood.repository.BillingRepository;
import com.pillgood.repository.PaymentRepository;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final PaymentRepository paymentRepository;
    private final BillingRepository billingRepository;

    public PaymentService(RestTemplate restTemplate, @Value("${toss.payments.secretKey}") String apiKey, PaymentRepository paymentRepository, 
            BillingRepository billingRepository) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
        this.billingRepository = billingRepository;
    }

    public BillingAuthResponse issueBillingKey(BillingAuthRequest request) {
        String url = "https://api.tosspayments.com/v1/billing/authorizations/issue";

        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BillingAuthRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<BillingAuthResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, BillingAuthResponse.class);

        return responseEntity.getBody();
    }

    public void saveBillingKey(String billingKey, String memberUniqueId) {
        Billing billing = new Billing();
        billing.setBillingKey(billingKey);
        billing.setMemberUniqueId(memberUniqueId);
        billingRepository.save(billing); // 인스턴스 메서드 호출
    }

    public Billing getBillingByMemberUniqueId(String memberUniqueId) {
        return billingRepository.findByMemberUniqueId(memberUniqueId).orElse(null);
    }

    public PaymentApproveResponse confirmBilling(BillingPaymentRequest request, String billingKey) {
        String url = "https://api.tosspayments.com/v1/billing/" + billingKey;
        System.out.println(url);
        System.out.println(request);
        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BillingPaymentRequest> requestEntity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<PaymentApproveResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, PaymentApproveResponse.class);
            return responseEntity.getBody();
        } catch (Exception e) {
            System.out.println("RestTemplate 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public PaymentApproveResponse approvePayment(PaymentApproveRequest approveRequest) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

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
