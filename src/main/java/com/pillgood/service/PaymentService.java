package com.pillgood.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
import com.pillgood.dto.BillingDto;
import com.pillgood.dto.BillingPaymentRequest;
import com.pillgood.dto.PaymentApproveRequest;
import com.pillgood.dto.PaymentApproveResponse;
import com.pillgood.entity.Billing;
import com.pillgood.entity.OrderDetail;
import com.pillgood.entity.Payment;
import com.pillgood.entity.Subscription;
import com.pillgood.repository.BillingRepository;
import com.pillgood.repository.OrderRepository;
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

    public PaymentService(RestTemplate restTemplate, @Value("${toss.payments.secretKey}") String apiKey, PaymentRepository paymentRepository, 
            BillingRepository billingRepository, SubscriptionRepository subscriptionRepository, OrderService orderService, 
            CartService cartService, HttpSession session)  {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
        this.billingRepository = billingRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.orderService = orderService;
        this.cartService = cartService;
        this.session = session;
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

    public BillingDto saveBillingKey(String billingKey, String memberUniqueId) {
        Billing billing = new Billing();
        billing.setBillingKey(billingKey);
        billing.setMemberUniqueId(memberUniqueId);
        Billing savedBilling = billingRepository.save(billing); // 인스턴스 메서드 호출
        return convertToDto(savedBilling);
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

        PaymentApproveResponse response = restTemplate.postForObject(url, requestEntity, PaymentApproveResponse.class);
        
        // 결제 승인 성공 시 콘솔에 로그 출력
        if (response != null) {
            System.out.println("결제 승인 성공: " + response);
            savePaymentDetails(request, response);
        } else {
            System.out.println("결제 승인 실패");
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
    
    // 결제 승인 후 결제 정보를 저장하고, 장바구니에서 결제한 상품을 삭제하는 메서드
    private void savePaymentDetails(PaymentApproveRequest approveRequest, PaymentApproveResponse approveResponse) {

        Payment payment = new Payment();
        payment.setPaymentNo(approveResponse.getPaymentKey());
        payment.setOrderNo(approveResponse.getOrderId());
        payment.setAmount(approveResponse.getTotalAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setMethod(approveResponse.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscriptionStatus(approveResponse.getType()); // 수정된 부분

        // 추가 정보를 설정할 수 있습니다.
        paymentRepository.save(payment);
        orderService.updateOrderStatusToPaid(approveResponse.getOrderId()); // 주문 상태 업데이트

        String memberId = (String) session.getAttribute("memberId");
        deletePurchasedItemsFromCart(memberId, approveRequest.getOrderId());
    }
    
    // BillingPaymentRequest에 대한 결제 정보 저장 메서드 추가
    private void savePaymentDetails(BillingPaymentRequest request, PaymentApproveResponse approveResponse) {

        Payment payment = new Payment();
        payment.setPaymentNo(approveResponse.getPaymentKey());
        payment.setOrderNo(approveResponse.getOrderId());
        payment.setAmount(approveResponse.getTotalAmount());
        payment.setStatus(approveResponse.getStatus());
        payment.setMethod(approveResponse.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscriptionStatus(approveResponse.getType()); // 수정된 부분

        // 추가 정보를 설정할 수 있습니다.
        paymentRepository.save(payment);
        
        Subscription subscription = new Subscription();
        subscription.setMemberUniqueId(request.getCustomerKey());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setSubscriptionStatus("T");
        subscription.setPaymentNo(approveResponse.getPaymentKey());
        
        subscriptionRepository.save(subscription);
        orderService.updateOrderStatusToPaid(approveResponse.getOrderId()); // 주문 상태 업데이트
        deletePurchasedItemsFromCart(request.getCustomerKey(), request.getOrderId());
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
}
