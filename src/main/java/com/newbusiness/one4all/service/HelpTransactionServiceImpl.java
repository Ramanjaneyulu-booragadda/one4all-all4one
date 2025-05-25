package com.newbusiness.one4all.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.HelpSubmissionDTO;
import com.newbusiness.one4all.dto.HelpVerificationDTO;
import com.newbusiness.one4all.dto.ReceiveHelpPageResponse;
import com.newbusiness.one4all.dto.ReceivedHelpDetailDTO;
import com.newbusiness.one4all.dto.ReceivedHelpSummaryDTO;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpTransactionServiceImpl implements HelpTransactionService {

    private final HelpSubmissionRepository helpSubmissionRepository;
    private final HelpSubmissionAuditLogRepository auditLogRepo;
    private final FileStorageService fileStorageService;
    private final MLMService mlmService;
    @Override
    public HelpSubmission submitHelpTransaction(HelpSubmissionDTO dto) throws Exception {
        log.info("Submitting help transaction: sender={}, receiver={}, level={}, amount={}", dto.getOfaMemberId(), dto.getReceiverMemberId(), dto.getUplinerLevel(), dto.getAmount());
        Optional<HelpSubmission> existing = helpSubmissionRepository
            .findBySenderMemberIdAndReceiverMemberIdAndUplinerLevelAndSubmissionStatusNot(
                dto.getOfaMemberId(),
                dto.getReceiverMemberId(),
                Integer.parseInt(dto.getUplinerLevel()),
                SubmissionStatus.REJECTED
            );
        if (existing.isPresent()) {
            HelpSubmission p = existing.get();
            log.warn("Duplicate payment attempt: sender={}, receiver={}, level={}, existingAmount={}", dto.getOfaMemberId(), dto.getReceiverMemberId(), dto.getUplinerLevel(), p.getSubmittedAmount());
            throw new DuplicatePaymentException(String.format(
                "Payment already submitted by helper %s to upliner %s for level %s with amount ₹%s",
                dto.getOfaMemberId(),
                dto.getReceiverMemberId(),
                dto.getUplinerLevel(),
                p.getSubmittedAmount()
            ));
        }
        Integer level = Integer.parseInt(dto.getUplinerLevel());
        BigDecimal expectedAmount = mlmService.getPayoutForPhase(level);
        if (expectedAmount == null || dto.getAmount().compareTo(expectedAmount) != 0) {
            log.error("Amount mismatch for help transaction: expected={}, actual={}, sender={}, receiver={}, level={}", expectedAmount, dto.getAmount(), dto.getOfaMemberId(), dto.getReceiverMemberId(), level);
            throw new IllegalArgumentException(
                String.format("Amount mismatch. Expected ₹%s but got ₹%s", expectedAmount, dto.getAmount())
            );
        }
        HelpSubmission submission = new HelpSubmission();
        submission.setSenderMemberId(dto.getOfaMemberId());
        submission.setReceiverMemberId(dto.getReceiverMemberId());
        submission.setUplinerLevel(level);
        submission.setSubmittedAmount(dto.getAmount());
        submission.setReceiverMobile(dto.getReceiverMobile());
        submission.setProof(dto.getProof());
        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submission.setTransactionDate(LocalDateTime.now());
        submission.setSubmissionReferenceId(UUID.randomUUID().toString());

        // 4️⃣ Save and audit
        HelpSubmission saved = helpSubmissionRepository.save(submission);
        log.info("Help transaction submitted successfully: referenceId={}, sender={}, receiver={}, amount={}", saved.getSubmissionReferenceId(), saved.getSenderMemberId(), saved.getReceiverMemberId(), saved.getSubmittedAmount());
        logAuditAction(saved, "SUBMITTED", dto.getOfaMemberId(), "Help initiated by user " + dto.getOfaMemberId());

        return saved;
    }


    @Override
    public HelpSubmission verifyHelpTransaction(HelpVerificationDTO dto) {
        log.info("Verifying help transaction: paymentId={}, status={}", dto.getPaymentId(), dto.getStatus());
        HelpSubmission payment = helpSubmissionRepository.findById(Long.valueOf(dto.getPaymentId()))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setSubmissionStatus(SubmissionStatus.valueOf(dto.getStatus()));
        payment.setComments(dto.getComments());
        payment.setVerifiedBy(payment.getReceiverMemberId());
        payment.setVerificationDate(LocalDateTime.now());

        HelpSubmission updated = helpSubmissionRepository.save(payment);
        log.info("Help transaction verified: paymentId={}, newStatus={}, verifiedBy={}", dto.getPaymentId(), dto.getStatus(), payment.getReceiverMemberId());
        logAuditAction(updated, dto.getStatus(), payment.getReceiverMemberId(), dto.getComments());
        return updated;
    }

    public ReceiveHelpPageResponse getReceivedHelps(String memberId) {
        log.info("Fetching received helps for memberId={}", memberId);
        List<ReceivedHelpDetailDTO> records = helpSubmissionRepository.getReceivedHelpDetails(memberId);
        ReceivedHelpSummaryDTO summary = helpSubmissionRepository.getReceivedHelpSummary(memberId);
        log.info("Fetched {} received help records for memberId={}", records.size(), memberId);
        return new ReceiveHelpPageResponse(records, summary);
    }

    @Override
    public List<HelpSubmission> getGivenHelps(String memberId) {
        log.info("Fetching given helps for memberId={}", memberId);
        return helpSubmissionRepository.findBySenderMemberId(memberId);
    }

    @Override
    public boolean isReceiverAuthorized(String paymentId, String loggedInUserId) {
        boolean authorized = helpSubmissionRepository.findById(Long.valueOf(paymentId))
                .map(p -> p.getReceiverMemberId().equals(loggedInUserId))
                .orElse(false);
        log.info("Receiver authorization check: paymentId={}, userId={}, authorized={}", paymentId, loggedInUserId, authorized);
        return authorized;
    }

    @Override
    public void logAuditAction(HelpSubmission payment, String action, String performedBy, String remarks) {
        log.info("Logging audit action: paymentId={}, action={}, performedBy={}, remarks={}", payment.getSubmissionReferenceId(), action, performedBy, remarks);
        HelpSubmissionAuditLog logEntry = new HelpSubmissionAuditLog();
        logEntry.setHelpSubmission(payment);
        logEntry.setAction(action);
        logEntry.setPerformedBy(performedBy);
        logEntry.setRemarks(remarks);
        logEntry.setTimestamp(LocalDateTime.now());
        auditLogRepo.save(logEntry);
    }

	
}

