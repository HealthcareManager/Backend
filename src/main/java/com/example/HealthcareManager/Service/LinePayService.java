package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Service
public class LinePayService {


    private String channelId ="2006384711";


    private String secretKey = "5b754d3c661a31399d318c968d4e5081";

    private static final String LINE_PAY_API_URL = "https://api-pay.line.me/v2/payments/request";
    private static final String CONFIRM_URL = "https://api-pay.line.me/v2/payments/{transactionId}/confirm";

    private final RestTemplate restTemplate = new RestTemplate();

    // 發送支付請求
    public String requestPayment(int amount, String currency, String orderId) {
        String requestUrl = LINE_PAY_API_URL;

        // 設置頭部 Authorization (Basic 認證)
        HttpHeaders headers = createHeaders();

        // 設置支付請求的 body，明確指定泛型類型為 <String, Object>
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("currency", currency);
        body.put("orderId", orderId);
        body.put("returnUrl", "http://localhost:8080/linepay/confirm");  // 支付完成後的返回 URL
        body.put("cancelUrl", "http://localhost:8080/linepay/cancel");  // 支付取消後的返回 URL

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 發送 POST 請求，回應的類型也是 <String, Object>
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, request, (Class<Map<String, Object>>)(Class)Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 成功取得支付 URL
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("info")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> info = (Map<String, Object>) responseBody.get("info");
                return (String) info.get("paymentUrl");
            }
        }

        return "支付請求失敗";
    }

    // 確認支付
    public String confirmPayment(String transactionId, int amount, String currency) {
        String confirmUrl = CONFIRM_URL.replace("{transactionId}", transactionId);

        // 設置頭部 Authorization (Basic 認證)
        HttpHeaders headers = createHeaders();

        // 設置確認支付的 body
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("currency", currency);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 發送 POST 確認請求
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(confirmUrl, HttpMethod.POST, request, (Class<Map<String, Object>>)(Class)Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return "支付確認成功";
        }

        return "支付確認失敗";
    }

    // 創建 Authorization 標頭
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = channelId + ":" + secretKey;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        headers.set("Authorization", authHeader);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
