package com.newbusiness.one4all.dto;

import java.math.BigDecimal;

public class PaymentDetailDTO {
	 private String ofaConsumerName;
	    private String ofaParentConsumerNo;
	    private String ofaStageNo;
	    private String ofaConsumerNo;
	    private String ofaMobile;
	    private BigDecimal ofaHelpAmount;
	    private String ofaPaymentStatus;
	    private BigDecimal ofaRefferalAmount;
		public String getOfaConsumerName() {
			return ofaConsumerName;
		}
		public void setOfaConsumerName(String ofaConsumerName) {
			this.ofaConsumerName = ofaConsumerName;
		}
		public String getOfaParentConsumerNo() {
			return ofaParentConsumerNo;
		}
		public void setOfaParentConsumerNo(String ofaParentConsumerNo) {
			this.ofaParentConsumerNo = ofaParentConsumerNo;
		}
	
		public String getOfaConsumerNo() {
			return ofaConsumerNo;
		}
		public void setOfaConsumerNo(String ofaConsumerNo) {
			this.ofaConsumerNo = ofaConsumerNo;
		}
		public String getOfaMobile() {
			return ofaMobile;
		}
		public void setOfaMobile(String ofaMobile) {
			this.ofaMobile = ofaMobile;
		}
		public BigDecimal getOfaHelpAmount() {
			return ofaHelpAmount;
		}
		public void setOfaHelpAmount(BigDecimal ofaHelpAmount) {
			this.ofaHelpAmount = ofaHelpAmount;
		}
		public String getOfaPaymentStatus() {
			return ofaPaymentStatus;
		}
		public void setOfaPaymentStatus(String ofaPaymentStatus) {
			this.ofaPaymentStatus = ofaPaymentStatus;
		}
		public String getOfaStageNo() {
			return ofaStageNo;
		}
		public void setOfaStageNo(String ofaStageNo) {
			this.ofaStageNo = ofaStageNo;
		}
		public BigDecimal getOfaRefferalAmount() {
			return ofaRefferalAmount;
		}
		public void setOfaRefferalAmount(BigDecimal ofaRefferalAmount) {
			this.ofaRefferalAmount = ofaRefferalAmount;
		}
		
	    
}
