package com.newbusiness.one4all.model;

import java.math.BigDecimal;
import java.util.Date;

import com.newbusiness.one4all.util.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
@Table(name = "upliner_payment_details", schema = "datalayer")
public class UplinerPaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_ref_id", nullable = false)
    private String transactionRefId; // Links to PaymentDetails.transactionRefId

    @Column(name = "upliner_id", nullable = false)
    @NotBlank(message = "{payment.ofaParentConsumerNo.notblank}")
    @Size(max = 18, message = "{payment.ofaParentConsumerNo.size}")
    private String uplinerId; // References the upliner's member ID

    @Column(name = "upliner_name", nullable = false)
    private String uplinerName;

    @Column(name = "upliner_level", nullable = false)
    @NotNull(message = "{payment.ofaStageNo.notblank}")
    private Integer uplinerLevel;

    @Column(name = "received_amount", nullable = false)
    private BigDecimal receivedAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // "SUCCESS", "FAILED", etc.

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
    
    @NotBlank(message = "{payment.ofaMobile.notblank}")
    @Size( min = 10 , max = 12, message = "{payment.ofaMobile.size}")
    @Column(name = "upliner_mobile")
    private String uplinerMobile;
    
}

