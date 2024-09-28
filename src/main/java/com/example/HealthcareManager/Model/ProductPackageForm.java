package com.example.HealthcareManager.Model;

import java.math.BigDecimal;
import java.util.List;

public class ProductPackageForm {
    private String id;
    private String name;
    private BigDecimal amount;
    private List<ProductForm> Products;

    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    public List<ProductForm> getProducts() {
        return Products;
    }
    public void setProducts(List<ProductForm> products) {
        Products = products;
    }

}
