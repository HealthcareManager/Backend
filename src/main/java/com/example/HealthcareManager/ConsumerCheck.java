package com.example.HealthcareManager;

import java.util.Arrays;
import java.util.Map;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
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
import com.example.HealthcareManager.Model.Consumer;
import com.example.HealthcareManager.Model.ProductForm;
import com.example.HealthcareManager.Model.ProductPackageForm;
import com.example.HealthcareManager.Model.RedirectUrls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // 引入 Jackson 的 ObjectMapper

public class ConsumerCheck {



public static String encrypt(final String keys, final String data) {
return toBase64String(HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, keys.getBytes()).doFinal(data.getBytes()));
}

    
    private static String toBase64String(byte[] bytes) {
        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);
}

    public static void main(String[] args) {
    ObjectMapper mapper = new ObjectMapper();
    CheckoutPaymentRequestForm form = new CheckoutPaymentRequestForm();

    form.setAmount(new BigDecimal("100"));
    form.setCurrency("TWD");
    form.setOrderId("merchant_order_id");

    ProductPackageForm productPackageForm = new ProductPackageForm();
    productPackageForm.setId("package_id");
    productPackageForm.setName("shop_name");
    productPackageForm.setAmount(new BigDecimal("100"));

    ProductForm productForm = new ProductForm();
    productForm.setId("product_id");
    productForm.setName("product_name");
    productForm.setImageUrl("");
    productForm.setQuantity(new BigDecimal("10"));
    productForm.setPrice(new BigDecimal("10"));
    productPackageForm.setProducts(Arrays.asList(productForm));

    form.setPackages(Arrays.asList(productPackageForm));
    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setConfirmUrl("");
    redirectUrls.setCancelUrl("");
    form.setRedirectUrls(redirectUrls);

    String ChannelSecret = "a917ab6a2367b536f8e5a6e2977e06f4";
    String requestUri = "/v3/payments/request";
    String nonce = UUID.randomUUID().toString();
    try {
        String signature = encrypt(ChannelSecret, ChannelSecret + requestUri + mapper.writeValueAsString(form) + nonce);
        System.out.println(signature);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
    }
}

