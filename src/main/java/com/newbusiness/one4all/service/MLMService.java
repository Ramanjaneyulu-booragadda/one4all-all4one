package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.model.UplinerPaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UplinerPaymentDetailsRepository;
import com.newbusiness.one4all.strategy.PaymentDistributionStrategy;
import com.newbusiness.one4all.util.PaymentStatus;

@Service
@Transactional
public class MLMService implements PaymentDistributionStrategy {

	@Autowired
	private Environment environment;

	@Autowired
	private UplinerPaymentDetailsRepository uplinerPaymentDetailsRepository;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private ReferralService referralService;

	/**
	 * Retrieve the referral bonus amount from properties.
	 */
	public BigDecimal getReferralBonusFromProperties() {
		return new BigDecimal(environment.getProperty("mlm.bonus.direct.referral", "1000"));
	}

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

	@Transactional
	public void distributePayment(PaymentDetails paymentDetails, List<UplinerWithMemberDetailsDTO> upliners) {
		BigDecimal totalReceivedAmount = paymentDetails.getOfaGivenAmount();
		Map<Integer, BigDecimal> phaseScheme = getPhaseBasedPayoutScheme();
		// Step 1: Fetch existing paid levels
		List<Integer> paidLevels = uplinerPaymentDetailsRepository
				.findPaidLevelsByConsumerNo(paymentDetails.getOfaConsumerNo());
		
		
		// Step 2: Start distributing from the specified stage (increment level dynamically)
	    int currentLevel = paymentDetails.getOfaStageNo();
	    int maxLevel = phaseScheme.size(); // Get the total number of levels in phaseScheme
	    
	    while (currentLevel <= maxLevel) {
	        BigDecimal threshold = phaseScheme.getOrDefault(currentLevel, BigDecimal.ZERO);
	        BigDecimal payout = getPayoutForPhase(currentLevel);


			if (totalReceivedAmount.compareTo(threshold) < 0) {
				break; // Stop if funds are insufficient
			}
			if (paidLevels.contains(currentLevel)) {
				currentLevel++;
				continue; // Skip already paid levels
			}
			// Validate if upliner exists for this level
			if (upliners.size() < currentLevel) {
				throw new IllegalArgumentException(String.format("No upliner available at level %d", currentLevel));
			}

			UplinerWithMemberDetailsDTO upliner = upliners.get(currentLevel - 1);

			// Proceed with payment
			UplinerPaymentDetails uplinerPayment = new UplinerPaymentDetails();
			uplinerPayment.setTransactionRefId(paymentDetails.getTransactionRefId());
			uplinerPayment.setUplinerId(upliner.getUplinerDetails().getOfaMemberId());
			uplinerPayment.setUplinerName(upliner.getUplinerDetails().getOfaFullName());
			uplinerPayment.setUplinerLevel(currentLevel);
			uplinerPayment.setReceivedAmount(payout);
			uplinerPayment.setStatus(PaymentStatus.RECEIVED);
			uplinerPayment.setCreatedAt(new Date());
			uplinerPayment.setUpdatedAt(new Date());
			uplinerPayment.setUplinerMobile(upliner.getUplinerDetails().getOfaMobileNo());
			uplinerPaymentDetailsRepository.save(uplinerPayment);
			// Get all payment records for the given consumer, ordered by the latest date
			List<PaymentDetails> paymentRecords = paymentDetailRepository
			        .findAllByConsumerNoOrderByCreatedAtDesc(upliner.getUplinerDetails().getOfaMemberId());

			// Iterate through the list and update only the latest payment
			if (!paymentRecords.isEmpty()) {
			    PaymentDetails latestPaymentRecord = paymentRecords.get(0); // Get the first record (latest)
			    latestPaymentRecord.setOfaTotalAmount(payout.add(latestPaymentRecord.getOfaTotalAmount()));
			    latestPaymentRecord.setOfaUpdatedAt(new Date());
			    paymentDetailRepository.save(latestPaymentRecord);
			}


			totalReceivedAmount = totalReceivedAmount.subtract(payout);
			// Increment level for the next iteration
	        currentLevel++;
		}

		paymentDetails.setOfaTotalAmount(totalReceivedAmount);
	}

	private Map<Integer, BigDecimal> getPhaseBasedPayoutScheme() {
		Map<Integer, BigDecimal> phaseScheme = new LinkedHashMap<>();

		phaseScheme.put(1, new BigDecimal(0)); // L1 -> 2000
		phaseScheme.put(2, new BigDecimal(0)); // L2 -> 1000
		phaseScheme.put(3, new BigDecimal(4000)); // L3 -> 1000
		phaseScheme.put(4, new BigDecimal(6500)); // L4 -> 2500
		phaseScheme.put(5, new BigDecimal(11500)); // L5 -> 5000
		phaseScheme.put(6, new BigDecimal(21500)); // L6 -> 10000
		phaseScheme.put(7, new BigDecimal(41500)); // L7 -> 20000
		phaseScheme.put(8, new BigDecimal(71500)); // L8 -> 30000
		phaseScheme.put(9, new BigDecimal(111500)); // L9 -> 40000
		phaseScheme.put(10, new BigDecimal(161500)); // L10 -> 50000
		return phaseScheme;
	}

	private BigDecimal getPayoutForPhase(int level) {
		switch (level) {
		case 1:
			return new BigDecimal(2000);
		case 2:
			return new BigDecimal(1000);
		case 3:
			return new BigDecimal(1000);
		case 4:
			return new BigDecimal(2500);
		case 5:
			return new BigDecimal(5000);
		case 6:
			return new BigDecimal(10000);
		case 7:
			return new BigDecimal(20000);
		case 8:
			return new BigDecimal(30000);
		case 9:
			return new BigDecimal(40000);
		case 10:
			return new BigDecimal(50000);
		default:
			return BigDecimal.ZERO;
		}
	}
}
