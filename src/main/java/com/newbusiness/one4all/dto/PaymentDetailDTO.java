package com.newbusiness.one4all.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentDetailDTO {
	private String ofaConsumerName;
	private String ofaParentConsumerNo;
	private Integer ofaStageNo;
	private String ofaConsumerNo;
	private String ofaMobile;
	private BigDecimal ofaGivenAmount;
	private String ofaPaymentStatus;
	private BigDecimal ofaRefferarMobile;

	
}
