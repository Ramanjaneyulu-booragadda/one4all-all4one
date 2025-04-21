package com.newbusiness.one4all.service;



import com.newbusiness.one4all.dto.HelpSubmissionDTO;
import com.newbusiness.one4all.dto.HelpVerificationDTO;
import com.newbusiness.one4all.dto.ReceiveHelpPageResponse;
import com.newbusiness.one4all.model.HelpSubmission;

import java.util.List;

public interface HelpTransactionService {
	HelpSubmission submitHelpTransaction(HelpSubmissionDTO dto) throws Exception;

	HelpSubmission verifyHelpTransaction(HelpVerificationDTO dto);

    ReceiveHelpPageResponse getReceivedHelps(String memberId);

    List<HelpSubmission> getGivenHelps(String memberId);

    boolean isReceiverAuthorized(String paymentId, String loggedInUserId);

    void logAuditAction(HelpSubmission payment, String action, String performedBy, String remarks);
}


