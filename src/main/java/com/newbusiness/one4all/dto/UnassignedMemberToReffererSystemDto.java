package com.newbusiness.one4all.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnassignedMemberToReffererSystemDto {
	private String ofaFullName;
    private String ofaMobileNo;
    private String ofaEmail;
    private String ofaCreatedDt;
    private String ofaCreatedBy;
    private List<String> roles;
}
