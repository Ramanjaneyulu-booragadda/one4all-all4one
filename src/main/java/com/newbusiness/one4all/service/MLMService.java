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
import com.newbusiness.one4all.model.UplinerPaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UplinerPaymentDetailsRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.strategy.PaymentDistributionStrategy;
import com.newbusiness.one4all.util.PaymentStatus;

@Service
@Transactional
public class MLMService implements PaymentDistributionStrategy{


	@Autowired
	private Environment environment;

	
	@Autowired
	private UplinerPaymentDetailsRepository uplinerPaymentDetailsRepository;
	
	/**
	 * Retrieve the referral bonus amount from properties.
	 */
	public BigDecimal getReferralBonusFromProperties() {
		return new BigDecimal(environment.getProperty("mlm.bonus.direct.referral", "1000"));
	}

	
	/**
	 * Pay upliners up to 10 stages.
	 */
	
//	public BigDecimal payUpliners(PaymentDetails paymentDetails, List<UplinerWithMemberDetailsDTO> uplinerDetailsList,
//			BigDecimal remainingBalance) {
//		Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();
//
//		for (int stage = 1; stage <= uplinerDetailsList.size(); stage++) {
//			UplinerWithMemberDetailsDTO upliner = uplinerDetailsList.get(stage - 1);
//			BigDecimal payout = calculateUplinePayout(payoutScheme, stage);
//
//			// Check if sufficient balance is available
//			if (remainingBalance.compareTo(payout) < 0) {
//				System.out.println("Insufficient balance to process upliner payout at stage: " + stage);
//				break; // Stop processing further upliners
//			} // Check if payment details already exist for the upliner
//			List<PaymentDetails> existingPaymentDetailsList = paymentDetailRepository
//					.findALLByOfaParentConsumerNo(upliner.getUplinerDetails().getOfaMemberId());
//
//			if (!existingPaymentDetailsList.isEmpty()) { // Update the existing payment details
//				PaymentDetails uplinerPayment = existingPaymentDetailsList.get(existingPaymentDetailsList.size() - 1);
//				uplinerPayment.setOfaTotalAmount(uplinerPayment.getOfaTotalAmount().subtract(payout));
//				// Add payout uplinerPayment.setOfaUpdatedAt(new Date()); // Update timestamp
//				paymentDetailRepository.save(uplinerPayment);
//				System.out.println(
//						"Updated payment details for upliner: " + upliner.getUplinerDetails().getOfaMemberId());
//			} else { // Create new payment details
//				PaymentDetails uplinerPayment = new PaymentDetails(); //
//				Optional<Member> referrerPerson = userRepository
//						.findByOfaMemberId(upliner.getUplinerDetails().getOfaMemberId());
//				
//				uplinerPayment.setOfaConsumerName(upliner.getUplinerDetails().getOfaFullName());
//				uplinerPayment.setOfaConsumerNo(upliner.getMemberId());
//				uplinerPayment.setOfaPaymentStatus(PaymentStatus.PAID);
//				uplinerPayment.setOfaCreatedAt(new Date());
//				uplinerPayment.setOfaUpdatedAt(new Date());
//				uplinerPayment.setOfaMobile(upliner.getConsumerMobile());
//				
//				paymentDetailRepository.save(uplinerPayment);
//				System.out.println(
//						"Created new payment details for upliner: " + upliner.getUplinerDetails().getOfaMemberId());
//			} // Deduct payout fromremaining balance
//			remainingBalance = remainingBalance.subtract(payout);
//		}
//		return remainingBalance;
//	}
	 
		/**
	 * Retrieve the payout scheme for upliners dynamically from properties.
	 */
	public Map<Integer, BigDecimal> getPayoutSchemeFromProperties() {
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

	@Override
    @Transactional
    public void distributePayment(PaymentDetails paymentDetails, List<UplinerWithMemberDetailsDTO> upliners) {
        BigDecimal remainingBalance = paymentDetails.getOfaGivenAmount();
        Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();

        for (int level = 1; level <= upliners.size(); level++) {
            UplinerWithMemberDetailsDTO upliner = upliners.get(level - 1);
            BigDecimal payout = payoutScheme.getOrDefault(level, BigDecimal.ZERO);

            if (remainingBalance.compareTo(payout) < 0) {
                break; // Stop if funds are insufficient
            }

            // Create upliner payment record
            UplinerPaymentDetails uplinerPayment = new UplinerPaymentDetails();
            uplinerPayment.setTransactionRefId(paymentDetails.getTransactionRefId());
            uplinerPayment.setUplinerId(upliner.getUplinerDetails().getOfaMemberId());
            uplinerPayment.setUplinerName(upliner.getUplinerDetails().getOfaFullName());
            uplinerPayment.setUplinerLevel(level);
            uplinerPayment.setReceivedAmount(payout);
            uplinerPayment.setStatus(PaymentStatus.RECEIVED);
            uplinerPayment.setCreatedAt(new Date());
            uplinerPayment.setUpdatedAt(new Date());
            uplinerPayment.setUplinerMobile(upliner.getUplinerDetails().getOfaMobileNo());
            uplinerPaymentDetailsRepository.save(uplinerPayment);

            remainingBalance = remainingBalance.subtract(payout);
        }

        paymentDetails.setOfaTotalAmount(remainingBalance);
    }
}
