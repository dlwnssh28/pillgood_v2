package com.pillgood.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pillgood.dto.PaymentRequest;
import com.pillgood.dto.PaymentResponse;

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

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");

        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);
        return restTemplate.postForObject(url, requestEntity, PaymentResponse.class);
    }
}
