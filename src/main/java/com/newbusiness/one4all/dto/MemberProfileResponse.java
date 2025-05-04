package com.newbusiness.one4all.dto;

import lombok.Data;

@Data
public class MemberProfileResponse {
    private String FullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String consumerNumber;
    private String parentConsumerNumber;
    private String accountCreatedDate;
    private String accountStatus;
}
