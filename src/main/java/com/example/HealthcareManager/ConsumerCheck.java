package com.example.HealthcareManager;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
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
import com.example.HealthcareManager.Model.ProductForm;
import com.example.HealthcareManager.Model.ProductPackageForm;
import com.example.HealthcareManager.Model.RedirectUrls;
import com.example.HealthcareManager.Repository.CheckoutPaymentRequestFormRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConsumerCheck {

    @Autowired
    private CheckoutPaymentRequestFormRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
}
