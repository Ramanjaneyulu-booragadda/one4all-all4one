package com.newbusiness.one4all.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface ReceivedHelpDetailDTO {
	String getMemberId();
	String getReceivedFrom();
	String getTransactionId();
	Date getRequestReceivedAt();
	Date getRequestModifiedAt();
	BigDecimal getReceivedAmount();
	String getStatus();
	String getPaymentID();
	String getProofDoc();
}

