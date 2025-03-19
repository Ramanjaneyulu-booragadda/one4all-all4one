package com.newbusiness.one4all.dto;

import java.math.BigDecimal;

import com.newbusiness.one4all.model.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//@AllArgsConstructor
@Setter
@Getter
public class UplinerWithMemberDetailsDTO {
    private Long id; // ID from UplinerDetails
    private String memberId; // Member ID from UplinerDetails
    private Integer uplinerLevel; // Upliner level
    private String consumerMobile;
    private Member uplinerDetails; // Full details of the upliner member
    private BigDecimal levelAmount;
    private String uplinerName;
    private String uplinerMemberId;
    private String uplinerMobileNo;
}