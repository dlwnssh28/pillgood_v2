package com.pillgood.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.pillgood.dto.BillingAuthRequest;
import com.pillgood.dto.BillingAuthResponse;
import com.pillgood.dto.BillingDto;
import com.pillgood.dto.BillingPaymentRequest;
import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.dto.PointDto;
import com.pillgood.entity.Billing;
import com.pillgood.entity.OrderDetail;
import com.pillgood.entity.Payment;
import com.pillgood.entity.Subscription;
import com.pillgood.repository.BillingRepository;
import com.pillgood.repository.PaymentRepository;
import com.pillgood.repository.SubscriptionRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final PaymentRepository paymentRepository;
    private final BillingRepository billingRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final OrderService orderService;
    private final CartService cartService;
    private final HttpSession session;
    private final PointService pointService;

    public PaymentService(RestTemplate restTemplate, @Value("${toss.payments.secretKey}") String apiKey, PaymentRepository paymentRepository, 
            BillingRepository billingRepository, SubscriptionRepository subscriptionRepository, OrderService orderService, 
            CartService cartService, HttpSession session, PointService pointService) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
        this.billingRepository = billingRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.orderService = orderService;
        this.cartService = cartService;
        this.session = session;
        this.pointService = pointService;
    }

    public BillingAuthResponse issueBillingKey(BillingAuthRequest request) {
        String url = "https://api.tosspayments.com/v1/billing/authorizations/issue";

        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BillingAuthRequest> requestEntity = new HttpEntity<>(request, headers);
        return restTemplate.postForEntity(url, requestEntity, BillingAuthResponse.class).getBody();
    }

    public BillingDto saveBillingKey(String billingKey, String memberUniqueId) {
        Billing billing = new Billing();
        billing.setBillingKey(billingKey);
        billing.setMemberUniqueId(memberUniqueId);
        Billing savedBilling = billingRepository.save(billing);
        return convertToDto(savedBilling);
    }

    public Billing getBillingByMemberUniqueId(String memberUniqueId) {
        return billingRepository.findByMemberUniqueId(memberUniqueId).orElse(null);
    }

    public PaymentApproveResponse confirmBilling(BillingPaymentRequest request, String billingKey) {
        String url = "https://api.tosspayments.com/v1/billing/" + billingKey;
        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BillingPaymentRequest> requestEntity = new HttpEntity<>(request, headers);
        PaymentApproveResponse response = restTemplate.postForObject(url, requestEntity, PaymentApproveResponse.class);

        if (response != null) {
            savePaymentDetails(request, response);
        }

        return response;
    }

    public PaymentApproveResponse approvePayment(PaymentApproveRequest approveRequest) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentApproveRequest> requestEntity = new HttpEntity<>(approveRequest, headers);
        PaymentApproveResponse response = restTemplate.postForObject(url, requestEntity, PaymentApproveResponse.class);

        if (response != null) {
            savePaymentDetails(approveRequest, response);
        }

        return response;
    }

    @Transactional
    private void savePaymentDetails(PaymentApproveRequest approveRequest, PaymentApproveResponse approveResponse) {
        Payment payment = new Payment();
        payment.setPaymentNo(approveResponse.getPaymentKey());
        payment.setOrderNo(approveResponse.getOrderId());
        payment.setAmount(approveResponse.getTotalAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setMethod(approveResponse.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscriptionStatus(approveResponse.getType());

        paymentRepository.save(payment);
        orderService.updateOrderStatusToPaid(approveResponse.getOrderId());

        String memberId = (String) session.getAttribute("memberId");
        deletePurchasedItemsFromCart(memberId, approveRequest.getOrderId());

        // 포인트 적립
        int pointsToSave = (int) (approveResponse.getTotalAmount() * 0.01);
        savePoints(memberId, pointsToSave, "ORDER", approveResponse.getOrderId());
    }

    @Transactional
    private void savePaymentDetails(BillingPaymentRequest request, PaymentApproveResponse approveResponse) {
        Payment payment = new Payment();
        payment.setPaymentNo(approveResponse.getPaymentKey());
        payment.setOrderNo(approveResponse.getOrderId());
        payment.setAmount(approveResponse.getTotalAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setMethod(approveResponse.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscriptionStatus(approveResponse.getType());

        paymentRepository.save(payment);

        Subscription subscription = new Subscription();
        subscription.setMemberUniqueId(request.getCustomerKey());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setSubscriptionStatus("T");
        subscription.setPaymentNo(approveResponse.getPaymentKey());

        subscriptionRepository.save(subscription);
        orderService.updateOrderStatusToPaid(approveResponse.getOrderId());
        deletePurchasedItemsFromCart(request.getCustomerKey(), request.getOrderId());

        int pointsToSave = (int) (approveResponse.getTotalAmount() * 0.01);
        savePoints(request.getCustomerKey(), pointsToSave, "BILLING", approveResponse.getOrderId());
    }

    private void deletePurchasedItemsFromCart(String memberUniqueId, String orderId) {
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);
        List<Integer> productIds = orderDetails.stream()
                                               .map(orderDetail -> orderDetail.getProduct().getProductId())
                                               .collect(Collectors.toList());

        cartService.deleteCarts(productIds, memberUniqueId);
    }

    private BillingDto convertToDto(Billing billing) {
        BillingDto billingDto = new BillingDto();
        billingDto.setBillingKey(billing.getBillingKey());
        billingDto.setMemberUniqueId(billing.getMemberUniqueId());
        return billingDto;
    }

    @Transactional
    private void savePoints(String memberUniqueId, int points, String reason, String orderId) {
        PointDto pointDto = new PointDto();
        pointDto.setMemberUniqueId(memberUniqueId);
        pointDto.setPoints(points);
        pointDto.setPointStatusCode("PS");
        pointDto.setTransactionDate(LocalDateTime.now());
        pointDto.setExpiryDate(LocalDateTime.now().plusYears(1));
        pointDto.setPointMasterId(reason);
        pointDto.setReferenceId(orderId);

        pointService.createPoint(pointDto);
    }
}