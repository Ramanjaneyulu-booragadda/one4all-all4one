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
import java.util.Date;

import com.newbusiness.one4all.util.PaymentStatus;
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
    @Size(max = 18, message = "{payment.ofaParentConsumerNo.size}")
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
    
    @NotBlank(message = "{payment.ofaMobile.notblank}")
    @Size( min = 10 , max = 12, message = "{payment.ofaMobile.size}")
    @Column(name = "ofa_contact_number")
    private String ofaRefferarMobile;

    @Column(name = "ofa_stage_no", nullable = false)
    @NotNull(message = "{payment.ofaStageNo.notblank}")
    private Integer ofaStageNo;


    @Enumerated(EnumType.STRING)
    @Column(name = "ofa_payment_status", nullable = false)
    private PaymentStatus ofaPaymentStatus;

    @Column(name = "ofa_countdown")
    private Integer ofaCountdown;

    @Column(name = "ofa_created_at", nullable = false)
    private Date ofaCreatedAt;

    @Column(name = "ofa_updated_at", nullable = false)
    private Date ofaUpdatedAt;
    
    @Column(name = "ofa_total_amount", nullable = false)
    private BigDecimal ofaTotalAmount;
	
    @Column(name = "ofa_transaction_ref_id", nullable = false, unique = true)
    private String transactionRefId;
}
