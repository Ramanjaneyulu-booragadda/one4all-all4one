package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.repository.HelpSubmissionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HelpDashboardService {

    @Autowired
    private HelpSubmissionRepository helpSubmissionRepository; // or HelpDashboardRepository

    public HelpDashboardProjection getHelpSummary(String memberId) {
        log.info("Fetching help summary for memberId={}", memberId);
        HelpDashboardProjection summary = helpSubmissionRepository.getHelpSummary(memberId);
        log.info("Fetched help summary for memberId={}: {}", memberId, summary);
        return summary;
    }
}

