package com.example.HealthcareManager.Model;

public class RedirectUrls {
    private String confirmUrl;
    private String cancelUrl;

    public String getCancelUrl() {
        return cancelUrl;
    }
    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
    public String getConfirmUrl() {
        return confirmUrl;
    }
    public void setConfirmUrl(String confirmUrl) {
        this.confirmUrl = confirmUrl;
    }
}
