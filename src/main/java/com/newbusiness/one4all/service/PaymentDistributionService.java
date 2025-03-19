package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.strategy.PaymentDistributionStrategy;

import jakarta.transaction.Transactional;

@Service
public class PaymentDistributionService {

	@Autowired
	private PaymentDistributionStrategy paymentDistributionStrategy;

	@Autowired
	private ReferralService referralService;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Transactional
	public void distributePayment(PaymentDetails paymentDetails) {
		List<UplinerWithMemberDetailsDTO> upliners = referralService.getUpliners(paymentDetails.getOfaConsumerNo());
		paymentDistributionStrategy.distributePayment(paymentDetails, upliners);
	}

	public PaymentDetails addPreviousPaymentsToCurrent(PaymentDetails paymentDetails) {
		// Fetch all existing payments for the consumer
		List<PaymentDetails> existingPaymentList = paymentDetailRepository
				.findAllByOfaConsumerNo(paymentDetails.getOfaConsumerNo());

		// Calculate the sum of ofaTotalAmount from all existing payments
		BigDecimal totalPreviousPayments = existingPaymentList.stream().map(PaymentDetails::getOfaTotalAmount) // Extract
																												// ofaTotalAmount
				.filter(amount -> amount != null) // Ensure non-null values
				.reduce(BigDecimal.ZERO, BigDecimal::add); // Sum all amounts

		// Add the totalPreviousPayments to the current ofaGivenAmount
		BigDecimal updatedGivenAmount = paymentDetails.getOfaGivenAmount().add(totalPreviousPayments);
		paymentDetails.setOfaGivenAmount(updatedGivenAmount);

		return paymentDetails;
	}
}
