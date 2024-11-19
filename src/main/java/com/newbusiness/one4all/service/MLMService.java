package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.PaymentStatus;

@Service
@Transactional
public class MLMService {

	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Retrieve the referral bonus amount from properties.
	 */
	public BigDecimal getReferralBonusFromProperties() {
		return new BigDecimal(environment.getProperty("mlm.bonus.direct.referral", "1000"));
	}

	/**
	 * Pay direct referral bonus to the referrer.
	 */
	public Map<PaymentDetails, BigDecimal> payDirectReferralBonus(PaymentDetails paymentDetails,
			BigDecimal referralBonus, BigDecimal remainingBalance) {
		Map<PaymentDetails, BigDecimal> objectwithBalance = new HashMap<>();
		if (remainingBalance.compareTo(referralBonus) < 0) {
			System.out.println("Insufficient balance for direct referral bonus.");
			return objectwithBalance; // Skip if balance is insufficient
		}
		PaymentDetails refpaymentDetails = null;
		Optional<Member> referrerPerson = userRepository.findByOfaMemberId(paymentDetails.getOfaParentConsumerNo());
		if (referrerPerson.isPresent()) {
			Member referrer = referrerPerson.get();
			// check his existing payment details if present
			Optional<PaymentDetails> refPayement = paymentDetailRepository
					.findByOfaConsumerNo(referrer.getOfaMemberId());
			if (refPayement.isPresent()) {
				// do update the payment
				refpaymentDetails = new PaymentDetails();
				refpaymentDetails.setOfaTotalAmount(referralBonus.add(refPayement.get().getOfaHelpAmount()));
				refpaymentDetails.setOfaUpdatedAt(new Date());
				refpaymentDetails.setOfaRefferalAmount(new BigDecimal(0));
				paymentDetailRepository.save(refpaymentDetails);
			} else {
				refpaymentDetails = new PaymentDetails();
				refpaymentDetails.setOfaConsumerName(paymentDetails.getOfaConsumerName());
				refpaymentDetails.setOfaParentConsumerNo(paymentDetails.getOfaParentConsumerNo());
				refpaymentDetails.setOfaConsumerNo(paymentDetails.getOfaConsumerNo());
				refpaymentDetails.setOfaRefferalAmount(referralBonus);
				refpaymentDetails.setOfaRefferarMobile(paymentDetails.getOfaRefferarMobile());
				refpaymentDetails.setOfaMobile(paymentDetails.getOfaMobile());
				refpaymentDetails.setOfaPaymentStatus(PaymentStatus.PAID);
				refpaymentDetails.setOfaUpdatedAt(new Date());
				refpaymentDetails.setOfaCreatedAt(new Date());
				refpaymentDetails.setOfaStageNo(paymentDetails.getOfaStageNo());
				refpaymentDetails.setOfaHelpAmount(paymentDetails.getOfaHelpAmount().subtract(referralBonus));
				refpaymentDetails.setOfaTotalAmount(paymentDetails.getOfaHelpAmount());
				paymentDetailRepository.save(refpaymentDetails);
			}
			// Deduct referral bonus from the remaining balance
			remainingBalance = remainingBalance.subtract(referralBonus);
		} else {
			throw new IllegalArgumentException(
					"Referrer does not exist for ParentConsumerNo: " + paymentDetails.getOfaParentConsumerNo());
		}

		objectwithBalance.put(refpaymentDetails, remainingBalance);
		return objectwithBalance;
	}

	/**
	 * Pay upliners up to 10 stages.
	 */
	public BigDecimal payUpliners(PaymentDetails paymentDetails, List<UplinerWithMemberDetailsDTO> uplinerDetailsList,
			BigDecimal remainingBalance) {
		Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();

		for (int stage = 1; stage <= uplinerDetailsList.size(); stage++) {
			UplinerWithMemberDetailsDTO upliner = uplinerDetailsList.get(stage - 1);
			BigDecimal payout = calculateUplinePayout(payoutScheme, stage);

			// Check if sufficient balance is available
			if (remainingBalance.compareTo(payout) < 0) {
				System.out.println("Insufficient balance to process upliner payout at stage: " + stage);
				break; // Stop processing further upliners
			}
			// Check if payment details already exist for the upliner
			List<PaymentDetails> existingPaymentDetailsList = paymentDetailRepository
					.findByOfaParentConsumerNo(upliner.getUplinerDetails().getOfaMemberId());

			if (!existingPaymentDetailsList.isEmpty()) {
				// Update the existing payment details
				PaymentDetails uplinerPayment = existingPaymentDetailsList.get(existingPaymentDetailsList.size() - 1);
				uplinerPayment.setOfaHelpAmount(uplinerPayment.getOfaHelpAmount().subtract(payout)); // Add payout
				uplinerPayment.setOfaUpdatedAt(new Date()); // Update timestamp
				paymentDetailRepository.save(uplinerPayment);
				System.out.println(
						"Updated payment details for upliner: " + upliner.getUplinerDetails().getOfaMemberId());
			} else {
				// Create new payment details
				PaymentDetails uplinerPayment = new PaymentDetails();
				// Optional<Member>
				// referrerPerson=userRepository.findByOfaMemberId(upliner.getUplinerDetails().getOfaMemberId());
				uplinerPayment.setOfaParentConsumerNo(upliner.getUplinerDetails().getOfaMemberId());
				uplinerPayment.setOfaConsumerName(upliner.getUplinerDetails().getOfaFullName());
				uplinerPayment.setOfaConsumerNo(upliner.getMemberId());
				uplinerPayment.setOfaHelpAmount(new BigDecimal(0).add(payout));
				uplinerPayment.setOfaPaymentStatus(PaymentStatus.PAID);
				uplinerPayment.setOfaStageNo(stage);
				uplinerPayment.setOfaCreatedAt(new Date());
				uplinerPayment.setOfaUpdatedAt(new Date());
				uplinerPayment.setOfaRefferalAmount(new BigDecimal(0));
				uplinerPayment.setOfaRefferarMobile(upliner.getUplinerDetails().getOfaMobileNo());
				uplinerPayment.setOfaMobile(upliner.getConsumerMobile());
				uplinerPayment.setOfaTotalAmount(
						uplinerPayment.getOfaHelpAmount().add(uplinerPayment.getOfaRefferalAmount()));
				paymentDetailRepository.save(uplinerPayment);
				System.out.println(
						"Created new payment details for upliner: " + upliner.getUplinerDetails().getOfaMemberId());
			}
			// Deduct payout from remaining balance
			remainingBalance = remainingBalance.subtract(payout);
		}
		return remainingBalance;
	}

	/**
	 * Retrieve the payout scheme for upliners dynamically from properties.
	 */
	private Map<Integer, BigDecimal> getPayoutSchemeFromProperties() {
		Map<Integer, BigDecimal> payoutScheme = new HashMap<>();
		for (int level = 1; level <= 10; level++) {
			String propertyKey = "mlm.payout.L" + level;
			String defaultValue = "1000";
			BigDecimal payout = new BigDecimal(environment.getProperty(propertyKey, defaultValue));
			payoutScheme.put(level, payout);
		}
		return payoutScheme;
	}

	/**
	 * Calculate the payout amount for a specific stage.
	 */
	private BigDecimal calculateUplinePayout(Map<Integer, BigDecimal> payoutScheme, int stage) {
		return payoutScheme.getOrDefault(stage, BigDecimal.ZERO);
	}

}
