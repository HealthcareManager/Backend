package com.example.HealthcareManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.HealthcareManager.Model.CheckoutPaymentRequestForm;
import com.example.HealthcareManager.Model.Payment;
import com.example.HealthcareManager.Model.ProductForm;
import com.example.HealthcareManager.Model.ProductPackageForm;
import com.example.HealthcareManager.Model.RedirectUrls;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.CheckoutPaymentRequestFormRepository;
import com.example.HealthcareManager.Repository.PaymentRepository;
import com.example.HealthcareManager.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ConsumerCheck {

    @Autowired
    private CheckoutPaymentRequestFormRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository; // 注入用戶倉庫

    private static final String CHANNEL_SECRET = "5b754d3c661a31399d318c968d4e5081"; // 測試用的 Channel Secret
    private static final String REQUEST_URI = "/v3/payments/request";

    // HMAC 加密方法
    public static String encrypt(final String keys, final String data) {
        return toBase64String(
                HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, keys.getBytes()).doFinal(data.getBytes()));
    }

    private static String toBase64String(byte[] bytes) {
        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);
    }

    public Map<String, Object> sendPaymentRequest(CheckoutPaymentRequestForm requestBody) {
        String nonce = UUID.randomUUID().toString();

        try {
            String body = objectMapper.writeValueAsString(requestBody);
            String signature = encrypt(CHANNEL_SECRET, CHANNEL_SECRET + REQUEST_URI + body + nonce);

            // 準備 Request Body 和 Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-LINE-ChannelId", "2006384711"); // 測試用的 Channel ID
            headers.add("X-LINE-Authorization-Nonce", nonce);
            headers.add("X-LINE-Authorization", signature);
            headers.add("X-LINE-MerchantDeviceType", "MOBILE");

            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
            String url = "https://sandbox-api-pay.line.me/v3/payments/request";
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // 返回原始響應
            if (response.getStatusCode().is2xxSuccessful()) {
                return Map.of("status", "success", "response", response.getBody());
            } else {
                return Map.of("status", "failed", "error", "Failed to send request");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Map.of("status", "failed", "error", "JSON Processing Error");
        } catch (RestClientException e) {
            e.printStackTrace();
            return Map.of("status", "failed", "error", e.getMessage());
        }
    }

    public void savePaymentRequest(CheckoutPaymentRequestForm requestForm) {

        // 確認 requestForm 內的資料有效
        System.out.println("Request Form details: " + requestForm.toString());

        // 將 CheckoutPaymentRequestForm 轉換為 Payment 實體
        Payment payment = new Payment();
        payment.setOrderId(requestForm.getOrderId());
        payment.setUserId(null); // 初始設為 null，等到支付成功後再寫入
        payment.setStatus("PENDING"); // 初始狀態設置為 "PENDING"

    
        // 保存到資料庫
        paymentRepository.save(payment);
    }
    
    public boolean updatePaymentStatus(String orderId, String userId, String status) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setUserId(userId); // 更新用戶 ID
            payment.setStatus(status); // 更新支付狀態

        // 設置支付成功的時間，當狀態為 "SUCCESS" 時
        if ("SUCCESS".equals(status)) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        // 先保存支付信息
        paymentRepository.save(payment);

        // 如果支付成功，更新用戶角色為 VIP
        if ("SUCCESS".equals(status)) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRole("VIP"); // 將用戶角色設置為 VIP
                userRepository.save(user); // 保存更新
            } else {
                throw new IllegalArgumentException("User not found");
            }
        }
        
            return true;
        }
        return false;
    }
    
}
