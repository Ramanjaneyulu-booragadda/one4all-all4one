package com.newbusiness.one4all.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.newbusiness.one4all.dto.HelpSubmissionDTO;
import com.newbusiness.one4all.dto.HelpVerificationDTO;
import com.newbusiness.one4all.dto.ReceiveHelpPageResponse;
import com.newbusiness.one4all.model.HelpSubmission;
import com.newbusiness.one4all.model.HelpSubmissionAuditLog;
import com.newbusiness.one4all.repository.HelpSubmissionAuditLogRepository;
import com.newbusiness.one4all.security.RoleCheck;
import com.newbusiness.one4all.service.HelpTransactionService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/help")
@RequiredArgsConstructor
public class HelpTransactionController {

    private final HelpTransactionService helpTransactionService;
    private final HelpSubmissionAuditLogRepository auditLogRepository;
    @RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @PostMapping(value="/give",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> giveHelp(@ModelAttribute HelpSubmissionDTO dto) throws Exception {
        String loggedInUser = SecurityUtils.getLoggedInMemberId();
        if (!loggedInUser.equals(dto.getOfaMemberId())) {
            ApiResponse error = ResponseUtils.buildSuccessResponse(Map.of("error", "Invalid user token for this request"), "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        HelpSubmission result = helpTransactionService.submitHelpTransaction(dto);
        ApiResponse response = ResponseUtils.buildSuccessResponse(result, "Help submitted successfully");
        return ResponseEntity.ok(response);
    }
    @RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyHelp(@RequestBody HelpVerificationDTO dto) {
        String loggedInUser = SecurityUtils.getLoggedInMemberId();
        boolean isAuthorized = helpTransactionService.isReceiverAuthorized(dto.getPaymentId(), loggedInUser);
        if (!isAuthorized) {
            ApiResponse error = ResponseUtils.buildSuccessResponse(Map.of("error", "You are not authorized to verify this transaction"), "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        HelpSubmission result = helpTransactionService.verifyHelpTransaction(dto);
        ApiResponse response = ResponseUtils.buildSuccessResponse(result, "Help verified successfully");
        return ResponseEntity.ok(response);
    }
    @RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @GetMapping("/receive-help/{memberId}")
    public ResponseEntity<ApiResponse> getReceivedHelp(@PathVariable String memberId) {
        String loggedInUser = SecurityUtils.getLoggedInMemberId();
        if (!loggedInUser.equals(memberId)) {
            ApiResponse error = ResponseUtils.buildSuccessResponse(Map.of("error", "Access denied for another user's received help"), "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        ReceiveHelpPageResponse helpPageResponse = helpTransactionService.getReceivedHelps(memberId);
        return ResponseEntity.ok(ResponseUtils.buildSuccessResponse(helpPageResponse, "Received helps retrieved"));
    }
    @RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @GetMapping("/given/{memberId}")
    public ResponseEntity<ApiResponse> getGivenHelp(@PathVariable String memberId) {
        String loggedInUser = SecurityUtils.getLoggedInMemberId();
        if (!loggedInUser.equals(memberId)) {
            ApiResponse error = ResponseUtils.buildSuccessResponse(Map.of("error", "Access denied for another user's given help"), "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        List<HelpSubmission> list = helpTransactionService.getGivenHelps(memberId);
        return ResponseEntity.ok(ResponseUtils.buildSuccessResponse(list, "Given helps retrieved"));
    }

    @GetMapping("/audit/{paymentId}")
    public ResponseEntity<ApiResponse> getAuditLogs(@PathVariable Long paymentId) {
        List<HelpSubmissionAuditLog> logs = auditLogRepository.findByHelpSubmission_Id(paymentId);
        return ResponseEntity.ok(ResponseUtils.buildSuccessResponse(logs, "Audit log retrieved"));
    }
}

