package com.newbusiness.one4all.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.HelpSubmissionDTO;
import com.newbusiness.one4all.dto.HelpVerificationDTO;
import com.newbusiness.one4all.exception.DuplicatePaymentException;
import com.newbusiness.one4all.model.HelpSubmission;
import com.newbusiness.one4all.model.HelpSubmissionAuditLog;
import com.newbusiness.one4all.repository.HelpSubmissionAuditLogRepository;
import com.newbusiness.one4all.repository.HelpSubmissionRepository;
import com.newbusiness.one4all.util.SubmissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HelpTransactionServiceImpl implements HelpTransactionService {

    private final HelpSubmissionRepository helpSubmissionRepository;
    private final HelpSubmissionAuditLogRepository auditLogRepo;
    private final FileStorageService fileStorageService;
    private final MLMService mlmService;
    @Override
    public HelpSubmission submitHelpTransaction(HelpSubmissionDTO dto) throws Exception {
        // 1️⃣ Check for duplicate submission (sender + receiver + level)
        Optional<HelpSubmission> existing = helpSubmissionRepository
            .findBySenderMemberIdAndReceiverMemberIdAndUplinerLevelAndSubmissionStatusNot(
                dto.getOfaMemberId(),
                dto.getReceiverMemberId(),
                Integer.parseInt(dto.getUplinerLevel()),
                SubmissionStatus.REJECTED // if using enum, convert to string
            );

        if (existing.isPresent()) {
            HelpSubmission p = existing.get();
            throw new DuplicatePaymentException(String.format(
                "Payment already submitted by helper %s to upliner %s for level %s with amount ₹%s",
                dto.getOfaMemberId(),
                dto.getReceiverMemberId(),
                dto.getUplinerLevel(),
                p.getSubmittedAmount()
            ));
        }

        // 2️⃣ Validate expected amount using level-based payout scheme
        Integer level = Integer.parseInt(dto.getUplinerLevel());
        BigDecimal expectedAmount = mlmService.getPayoutForPhase(level); // from your MLMService or payout config

        if (expectedAmount == null || dto.getAmount().compareTo(expectedAmount) != 0) {
            throw new IllegalArgumentException(
                String.format("Amount mismatch. Expected ₹%s but got ₹%s", expectedAmount, dto.getAmount())
            );
        }

        // 3️⃣ Upload file and build HelpSubmission record
        String fileUrl = fileStorageService.uploadProofFile(dto.getProofUrl());

        HelpSubmission submission = new HelpSubmission();
        submission.setSenderMemberId(dto.getOfaMemberId());
        submission.setReceiverMemberId(dto.getReceiverMemberId());
        submission.setUplinerLevel(level);
        submission.setSubmittedAmount(dto.getAmount());
        submission.setReceiverMobile(dto.getReceiverMobile());
        submission.setProofUrl(fileUrl);
        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submission.setCreatedAt(new Date());
        submission.setUpdatedAt(new Date());
        submission.setSubmissionReferenceId(UUID.randomUUID().toString());

        // 4️⃣ Save and audit
        HelpSubmission saved = helpSubmissionRepository.save(submission);

        logAuditAction(saved, "SUBMITTED", dto.getOfaMemberId(), "Help initiated by user " + dto.getOfaMemberId());

        return saved;
    }


    @Override
    public HelpSubmission verifyHelpTransaction(HelpVerificationDTO dto) {
        HelpSubmission payment = helpSubmissionRepository.findById(Long.valueOf(dto.getPaymentId()))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setSubmissionStatus(SubmissionStatus.valueOf(dto.getStatus()));
        payment.setComments(dto.getComments());
        payment.setUpdatedDate(LocalDateTime.now());
        payment.setVerifiedBy(payment.getReceiverMemberId());
        payment.setVerificationDate(LocalDateTime.now());

        HelpSubmission updated = helpSubmissionRepository.save(payment);
        logAuditAction(updated, dto.getStatus(), payment.getReceiverMemberId(), dto.getComments());
        return updated;
    }

    @Override
    public List<HelpSubmission> getReceivedHelps(String memberId) {
        return helpSubmissionRepository.findByReceiverMemberId(memberId);
    }

    @Override
    public List<HelpSubmission> getGivenHelps(String memberId) {
        return helpSubmissionRepository.findBySenderMemberId(memberId);
    }

    @Override
    public boolean isReceiverAuthorized(String paymentId, String loggedInUserId) {
        return helpSubmissionRepository.findById(Long.valueOf(paymentId))
                .map(p -> p.getReceiverMemberId().equals(loggedInUserId))
                .orElse(false);
    }

    @Override
    public void logAuditAction(HelpSubmission payment, String action, String performedBy, String remarks) {
    	HelpSubmissionAuditLog log = new HelpSubmissionAuditLog();
        log.setHelpSubmission(payment);
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setRemarks(remarks);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepo.save(log);
    }

	
}

