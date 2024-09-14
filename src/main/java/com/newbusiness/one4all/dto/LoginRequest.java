package com.newbusiness.one4all.dto;

public class LoginRequest {
    private String ofaMemberId;
    private String ofaPassword;

    // Getters and Setters
    public String getOfaMemberId() {
        return ofaMemberId;
    }

    public void setOfaMemberId(String ofaMemberId) {
        this.ofaMemberId = ofaMemberId;
    }

    public String getOfaPassword() {
        return ofaPassword;
    }

    public void setOfaPassword(String ofaPassword) {
        this.ofaPassword = ofaPassword;
    }
}
