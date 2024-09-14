package com.newbusiness.one4all.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ofa_payment_details",schema = "datalayer")
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ofa_payment_id")
    private Long ofaPaymentId;

    @Column(name = "ofa_consumer_no", nullable = false)
    @NotBlank(message = "{payment.ofaConsumerNo.notblank}")
    @Size(max = 15, message = "{payment.ofaConsumerNo.size}")
    private String ofaConsumerNo;
    @Column(name = "ofa_parent_consumer_no", nullable = false)
    @NotBlank(message = "{payment.ofaParentConsumerNo.notblank}")
    @Size(max = 15, message = "{payment.ofaParentConsumerNo.size}")
    private String ofaParentConsumerNo;
    @Column(name = "ofa_consumer_name", nullable = false)
    @NotBlank(message = "{payment.ofaConsumerName.notblank}")
    @Size(max = 255, message = "{payment.ofaConsumerName.size}")
    private String ofaConsumerName;

    @Column(name = "ofa_help_amount", nullable = false)
    @NotNull(message = "{payment.ofaHelpAmount.notblank}")
    private BigDecimal ofaHelpAmount;
    @Column(name = "ofa_refferal_amount")
    private BigDecimal ofaRefferalAmount;
    
    @Column(name = "ofa_mobile")
    @NotBlank(message = "{payment.ofaMobile.notblank}")
    @Size( min = 10 , max = 12, message = "{payment.ofaMobile.size}")
    private String ofaMobile;

    @Column(name = "ofa_contact_number")
    private String ofaContactNumber;

    @Column(name = "ofa_stage_no", nullable = false)
    @NotNull(message = "{payment.ofaStageNo.notblank}")
    private Integer ofaStageNo;


    @Enumerated(EnumType.STRING)
    @Column(name = "ofa_payment_status", nullable = false)
    private OfaPaymentStatus ofaPaymentStatus;

    @Column(name = "ofa_countdown")
    private Integer ofaCountdown;

    @Column(name = "ofa_created_at", nullable = false)
    private LocalDateTime ofaCreatedAt;

    @Column(name = "ofa_updated_at", nullable = false)
    private LocalDateTime ofaUpdatedAt;

	public Long getOfaPaymentId() {
		return ofaPaymentId;
	}

	public void setOfaPaymentId(Long ofaPaymentId) {
		this.ofaPaymentId = ofaPaymentId;
	}

	public String getOfaConsumerNo() {
		return ofaConsumerNo;
	}

	public void setOfaConsumerNo(String ofaConsumerNo) {
		this.ofaConsumerNo = ofaConsumerNo;
	}

	public String getOfaParentConsumerNo() {
		return ofaParentConsumerNo;
	}

	public void setOfaParentConsumerNo(String ofaParentConsumerNo) {
		this.ofaParentConsumerNo = ofaParentConsumerNo;
	}

	public String getOfaConsumerName() {
		return ofaConsumerName;
	}

	public void setOfaConsumerName(String ofaConsumerName) {
		this.ofaConsumerName = ofaConsumerName;
	}

	public BigDecimal getOfaHelpAmount() {
		return ofaHelpAmount;
	}

	public void setOfaHelpAmount(BigDecimal ofaHelpAmount) {
		this.ofaHelpAmount = ofaHelpAmount;
	}

	public BigDecimal getOfaRefferalAmount() {
		return ofaRefferalAmount;
	}

	public void setOfaRefferalAmount(BigDecimal ofaRefferalAmount) {
		this.ofaRefferalAmount = ofaRefferalAmount;
	}

	public String getOfaMobile() {
		return ofaMobile;
	}

	public void setOfaMobile(String ofaMobile) {
		this.ofaMobile = ofaMobile;
	}

	public String getOfaContactNumber() {
		return ofaContactNumber;
	}

	public void setOfaContactNumber(String ofaContactNumber) {
		this.ofaContactNumber = ofaContactNumber;
	}

	public Integer getOfaStageNo() {
		return ofaStageNo;
	}

	public void setOfaStageNo(Integer ofaStageNo) {
		this.ofaStageNo = ofaStageNo;
	}

	public OfaPaymentStatus getOfaPaymentStatus() {
		return ofaPaymentStatus;
	}

	public void setOfaPaymentStatus(OfaPaymentStatus ofaPaymentStatus) {
		this.ofaPaymentStatus = ofaPaymentStatus;
	}

	public Integer getOfaCountdown() {
		return ofaCountdown;
	}

	public void setOfaCountdown(Integer ofaCountdown) {
		this.ofaCountdown = ofaCountdown;
	}

	public LocalDateTime getOfaCreatedAt() {
		return ofaCreatedAt;
	}

	public void setOfaCreatedAt(LocalDateTime ofaCreatedAt) {
		this.ofaCreatedAt = ofaCreatedAt;
	}

	public LocalDateTime getOfaUpdatedAt() {
		return ofaUpdatedAt;
	}

	public void setOfaUpdatedAt(LocalDateTime ofaUpdatedAt) {
		this.ofaUpdatedAt = ofaUpdatedAt;
	}

    // Getters and setters omitted for brevity
    
}

enum OfaPaymentStatus {
    PAID, UNPAID, PENDING, CANCELLED
}
