package com.newbusiness.one4all.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String mobile; // New field for mobile-based reset
    private String token;
    private Instant expiryTime;
    private boolean used = false;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Instant getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(Instant expiryTime) {
		this.expiryTime = expiryTime;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public PasswordResetToken(Long id, String email, String mobile, String token, Instant expiryTime, boolean used) {
		super();
		this.id = id;
		this.email = email;
		this.mobile = mobile;
		this.token = token;
		this.expiryTime = expiryTime;
		this.used = used;
	}
	public PasswordResetToken() {
		super();
	}

    // Getters, Setters, Constructors
    
}