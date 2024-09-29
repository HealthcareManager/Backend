package com.example.HealthcareManager.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product_package_form")
public class ProductPackageForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵自動生成
    private Long id;

    private String name;
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "checkout_payment_request_form_id")
    private CheckoutPaymentRequestForm checkoutPaymentRequestForm;

    @OneToMany(mappedBy = "productPackageForm", cascade = CascadeType.ALL)
    private List<ProductForm> products;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CheckoutPaymentRequestForm getCheckoutPaymentRequestForm() {
        return checkoutPaymentRequestForm;
    }

    public void setCheckoutPaymentRequestForm(CheckoutPaymentRequestForm checkoutPaymentRequestForm) {
        this.checkoutPaymentRequestForm = checkoutPaymentRequestForm;
    }

    public List<ProductForm> getProducts() {
        return products;
    }

    public void setProducts(List<ProductForm> products) {
        this.products = products;
    }
}
