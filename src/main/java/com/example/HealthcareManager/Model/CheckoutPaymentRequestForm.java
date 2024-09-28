package com.example.HealthcareManager.Model;


import java.math.BigDecimal;
import java.util.List;

public class CheckoutPaymentRequestForm {
    private BigDecimal amount;
    private String currency;
    private String orderId;
    private List<ProductPackageForm> packages;
    private RedirectUrls redirectUrls;
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public List<ProductPackageForm> getPackages() {
        return packages;
    }
    public void setPackages(List<ProductPackageForm> packages) {
        this.packages = packages;
    }
    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }
    public void setRedirectUrls(RedirectUrls redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

}
