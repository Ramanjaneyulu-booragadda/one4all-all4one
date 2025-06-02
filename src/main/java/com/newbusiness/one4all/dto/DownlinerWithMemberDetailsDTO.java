package com.newbusiness.one4all.dto;

import com.newbusiness.one4all.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class DownlinerWithMemberDetailsDTO {
    private Long id; // ID from ReferrerDetails
    private String memberId; // Downliner member ID
    private Integer referralLevel; // Referral level
    private Member downlinerMemberDetails; // Full details of the downliner member
}