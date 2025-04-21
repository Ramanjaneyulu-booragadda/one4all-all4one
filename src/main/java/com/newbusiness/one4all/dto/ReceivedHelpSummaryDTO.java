package com.newbusiness.one4all.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface ReceivedHelpSummaryDTO {
	String getMemberId();
	BigDecimal getThisMonthTotal();
	BigDecimal getTotalReceivedAmount();
    Integer getRejectedRequestCount();
    Integer getApprovedRequestCount();
    Integer getTotalReceivedRequestCount();
}

