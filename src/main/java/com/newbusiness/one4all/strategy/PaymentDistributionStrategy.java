package com.newbusiness.one4all.strategy;

import java.util.List;

import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.PaymentDetails;

public interface PaymentDistributionStrategy {
	void distributePayment(PaymentDetails paymentDetails, List<UplinerWithMemberDetailsDTO> upliners);
}
