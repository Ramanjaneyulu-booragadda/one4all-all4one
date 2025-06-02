package com.newbusiness.one4all.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.newbusiness.one4all.util.SubmissionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "help_submission", schema = "one4all")
public class HelpSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_member_id", nullable = false)
    private String senderMemberId;

    @Column(name = "receiver_member_id", nullable = false)
    private String receiverMemberId;

    @Column(name = "upliner_level")
    private Integer uplinerLevel;

    @Column(name = "submitted_amount", nullable = false)
    private BigDecimal submittedAmount;

    @Column(name = "proof")
    private String proof;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_status", nullable = false)
    private SubmissionStatus submissionStatus;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;


    @Column(name = "submission_reference_id")
    private String submissionReferenceId;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @Column(name = "receiver_mobile")
    private String receiverMobile;

}
