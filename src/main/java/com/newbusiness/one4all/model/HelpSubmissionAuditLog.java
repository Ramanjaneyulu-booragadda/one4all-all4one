package com.newbusiness.one4all.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry for actions performed on help submissions.
 */
@Getter
@Setter
@Entity
@Table(name = "help_submission_audit_log", schema = "one4all")
public class HelpSubmissionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_help_submission_audit"))
    private HelpSubmission helpSubmission;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(nullable = false)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
