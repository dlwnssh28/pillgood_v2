package com.pillgood.controller;

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
            return ResponseEntity.status(500).build();
        }
    }
}
