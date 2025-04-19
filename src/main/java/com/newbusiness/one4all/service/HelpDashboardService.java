package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.repository.HelpSubmissionRepository;

@Service
public class HelpDashboardService {

    @Autowired
    private HelpSubmissionRepository helpSubmissionRepository; // or HelpDashboardRepository

    public HelpDashboardProjection getHelpSummary(String memberId) {
        return helpSubmissionRepository.getHelpSummary(memberId);
    }
}

