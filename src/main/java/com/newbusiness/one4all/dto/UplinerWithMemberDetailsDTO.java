package com.newbusiness.one4all.dto;

import com.newbusiness.one4all.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UplinerWithMemberDetailsDTO {
    private Long id; // ID from UplinerDetails
    private String memberId; // Member ID from UplinerDetails
    private Integer uplinerLevel; // Upliner level
    private String consumerMobile;
    private Member uplinerDetails; // Full details of the upliner member
}