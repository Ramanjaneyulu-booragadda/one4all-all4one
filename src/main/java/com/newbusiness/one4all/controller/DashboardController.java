package com.newbusiness.one4all.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.service.HelpDashboardService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.ResponseUtils;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private HelpDashboardService helpDashboardService;

    @GetMapping("/summary/{memberId}")
    public ResponseEntity<ApiResponse> getHelpSummary(@PathVariable String memberId) {
        HelpDashboardProjection summary = helpDashboardService.getHelpSummary(memberId);
        return ResponseEntity.ok(ResponseUtils.buildSuccessResponse(summary, "Help Summary Retrieved"));
    }
}

