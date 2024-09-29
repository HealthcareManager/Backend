package com.example.HealthcareManager.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "checkout_payment_request_form")
public class CheckoutPaymentRequestForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵自動生成
    private Long id;

    private BigDecimal amount;
    private String currency;
    private String orderId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "redirect_urls_id")
    private RedirectUrls redirectUrls;

    @OneToMany(mappedBy = "checkoutPaymentRequestForm", cascade = CascadeType.ALL)
    private List<ProductPackageForm> packages;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(RedirectUrls redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    public List<ProductPackageForm> getPackages() {
        return packages;
    }

    public void setPackages(List<ProductPackageForm> packages) {
        this.packages = packages;
    }
}
