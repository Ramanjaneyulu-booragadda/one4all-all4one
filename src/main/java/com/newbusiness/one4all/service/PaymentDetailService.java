package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.strategy.PaymentDistributionStrategy;
import com.newbusiness.one4all.util.PaymentStatus;
import com.newbusiness.one4all.util.ResponseUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentDetailService {
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PaymentDistributionService paymentDistributionService;
	@Autowired
	private ReferralService referralService;

	public PaymentDetails addPayment(PaymentDetails paymentDetails) {
		validatePayment(paymentDetails);
		 // Step 1: Validate consumer existence
        boolean consumerExists = userRepository.existsByOfaMemberId(paymentDetails.getOfaConsumerNo());
        if (!consumerExists) {
            throw new IllegalArgumentException("Consumer with ID " + paymentDetails.getOfaConsumerNo() + " does not exist.");
        }
		paymentDetails.setTransactionRefId(ResponseUtils.generateCorrelationID());
		paymentDetails.setOfaPaymentStatus(PaymentStatus.PAID);
		paymentDetails.setOfaCreatedAt(new Date());
		paymentDetails.setOfaUpdatedAt(new Date());
		//paymentDistributionService.addPreviousPaymentsToCurrent(paymentDetails);
		//paymentDetails.setOfaGivenAmount(updatedGivenAmount);
				
		paymentDetailRepository.save(paymentDetails);

		// Distribute payments using dedicated service
		paymentDistributionService.distributePayment(paymentDetails);

		// Update payment details after distribution
		paymentDetailRepository.save(paymentDetails);

		return paymentDetails;
	}

	private void validatePayment(PaymentDetails paymentDetails) {
	    if (paymentDetails.getOfaGivenAmount().compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Given amount must be greater than zero.");
	    }

	    // Check if consumer exists
	    boolean consumerExists = userRepository.existsByOfaMemberId(paymentDetails.getOfaConsumerNo());
	    if (!consumerExists) {
	        throw new IllegalArgumentException("Consumer with ID " + paymentDetails.getOfaConsumerNo() + " does not exist.");
	    }

	    // Ensure only direct children can make direct payments via upliner relationship
	    boolean isDirectChild = referralService.isDirectChildFromUplinerDetails(
	        paymentDetails.getOfaConsumerNo()
	    );
	    if (!isDirectChild) {
	        throw new IllegalArgumentException("Payment can only be made by direct children linked via upliner relationship.");
	    }
	}


	public PaymentDetails updatePayment(PaymentDetails paymentDetails) {
		List<PaymentDetails> existingPaymentList = paymentDetailRepository
				.findAllByOfaConsumerNo(paymentDetails.getOfaConsumerNo());
		PaymentDetails existingPaymentObj = null;
		if (!existingPaymentList.isEmpty()) {
			existingPaymentObj = existingPaymentList.get(0);
			existingPaymentObj.setOfaUpdatedAt(new Date());
			paymentDetailRepository.save(existingPaymentObj);
		}
		return existingPaymentObj;
	}

	// READ
	public Optional<PaymentDetails> getPaymentById(Long id) {
		return paymentDetailRepository.findById(id);
	}

	// READ ALL
	public List<PaymentDetails> getAllPayments() {
		return paymentDetailRepository.findAll();
	}

	

	// DELETE
	public boolean deletePayment(Long id) {
		Optional<PaymentDetails> existingPaymentDetails = paymentDetailRepository.findById(id);
		if (existingPaymentDetails.isPresent()) {
			paymentDetailRepository.deleteById(id);
			return true;
		}
		return false;
	}

	

	public int determineReferralLevel(PaymentDetails paymentDetails) {
		// Fetch the member who is being referred
		Optional<Member> member = userRepository.findByOfaMemberId(paymentDetails.getOfaConsumerNo());

		if (member.isPresent()) {
			Member referredMember = member.get();
			int level = 0; // Start at level 1
			Set<Long> visitedMembers = new HashSet<>(); // To track visited members

			/*
			 * Member referrer = referredMember.getReferredBy();
			 * 
			 * // Traverse the referredBy chain to calculate the level while (referrer !=
			 * null) { // Check for cyclic reference (infinite loop prevention) if
			 * (visitedMembers.contains(referrer.getOfaId())) { // Break the loop if we
			 * detect a cycle
			 * System.out.println("Cyclic reference detected in referral chain!"); break; }
			 * 
			 * // Add the current referrer to the visited set
			 * visitedMembers.add(referrer.getOfaId());
			 * 
			 * level++; referrer.setReferralLevel(level); // Determine and set level
			 * userRepository.save(referrer); // Save the updated level of referrer after
			 * adding a member referrer = referrer.getReferredBy(); // Move up the chain }
			 */

			return level;
		} else {
			// Default level if something goes wrong
			return 1;
		}
	}

	// Method to check for existing payment details by ofaConsumerNo
	public Optional<PaymentDetails> findByOfaConsumerNo(String ofaConsumerNo) {
		return paymentDetailRepository.findByOfaConsumerNo(ofaConsumerNo);
	}
}
