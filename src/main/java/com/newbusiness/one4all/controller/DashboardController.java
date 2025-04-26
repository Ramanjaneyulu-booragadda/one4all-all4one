package com.newbusiness.one4all.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.security.RoleCheck;
import com.newbusiness.one4all.service.HelpDashboardService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.SecurityUtils;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private HelpDashboardService helpDashboardService;
    @RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @GetMapping("/summary/{memberId}")
    public ResponseEntity<ApiResponse> getHelpSummary(@PathVariable String memberId) {
    	 String loggedInUser = SecurityUtils.getLoggedInMemberId();
         if (!loggedInUser.equals(memberId)) {
             ApiResponse error = ResponseUtils.buildSuccessResponse(Map.of("error", "Invalid user token for this request"), "Unauthorized");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
         }
    	HelpDashboardProjection summary = helpDashboardService.getHelpSummary(memberId);
        return ResponseEntity.ok(ResponseUtils.buildSuccessResponse(summary, "Help Summary Retrieved"));
    }
}

