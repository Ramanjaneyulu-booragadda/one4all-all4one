package com.newbusiness.one4all.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownlinerHelpInfoDto {
    private String memberId;
    private String fullName;
    private int level;
    private BigDecimal expectedAmount;
}