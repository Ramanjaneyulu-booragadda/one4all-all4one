package com.newbusiness.one4all.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String uplinerId; // References the upliner's member ID

    @Column(name = "upliner_name", nullable = false)
    private String uplinerName;

    @Column(name = "upliner_level", nullable = false)
    private Integer uplinerLevel;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status; // "SUCCESS", "FAILED", etc.

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}

