package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.PaymentStatus;

@Service
@Transactional(rollbackFor = Exception.class)
public class PaymentDetailService {
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;
	@Autowired
	private UserRepository userRepository;

	public PaymentDetails addPayment(PaymentDetails paymentDetails) {
		// Here you can implement other registration logic, like encrypting the password
		paymentDetails.setOfaCreatedAt(new Date());
		paymentDetails.setOfaUpdatedAt(new Date());
		paymentDetails.setOfaPaymentStatus(PaymentStatus.PAID);
		paymentDetails.setOfaTotalAmount(paymentDetails.getOfaHelpAmount().add(paymentDetails.getOfaRefferalAmount()));
		return paymentDetailRepository.save(paymentDetails);
	}

	public PaymentDetails updatePayment(PaymentDetails paymentDetails) {
		List<PaymentDetails> existingPaymentList = paymentDetailRepository
				.findAllByOfaConsumerNo(paymentDetails.getOfaConsumerNo());
		PaymentDetails existingPaymentObj = null;
		if (!existingPaymentList.isEmpty()) {
			existingPaymentObj = existingPaymentList.get(0);
			existingPaymentObj.setOfaHelpAmount(paymentDetails.getOfaHelpAmount());
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

	// UPDATE
	public Optional<PaymentDetails> updatePayment(Long id, PaymentDetailDTO dto) {
		Optional<PaymentDetails> existingPaymentDetails = paymentDetailRepository.findById(id);
		if (existingPaymentDetails.isPresent()) {
			PaymentDetails updatedPaymentDetails = convertToEntity(dto);
			updatedPaymentDetails.setOfaPaymentId(id); // Ensure the ID remains the same
			// Additional update logic can be added here
			return Optional.of(paymentDetailRepository.save(updatedPaymentDetails));
		}
		return Optional.empty();
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

	// Convert DTO to Entity (this should handle conversion logic)
	private PaymentDetails convertToEntity(PaymentDetailDTO dto) {
		// Implement the conversion logic from DTO to PaymentDetail entity
		return new PaymentDetails();
	}

	public void addRefererWithPayment(PaymentDetails paymentDetails) throws IllegalStateException {
		PaymentDetails savedPayment = addPayment(paymentDetails);

		Optional<Member> referrerOpt = userRepository.findByOfaMemberId(paymentDetails.getOfaParentConsumerNo());
		Optional<Member> referredMemberOpt = userRepository.findByOfaMemberId(paymentDetails.getOfaConsumerNo());

		if (referrerOpt.isPresent() && referredMemberOpt.isPresent()) {
			Member referredMember = referredMemberOpt.get();
			Member referrer = referrerOpt.get();

			/*
			 * if (referrer.getDownliners().size() >= 2) { throw new
			 * IllegalStateException("Referrer already has 2 direct members."); }
			 * 
			 * referredMember.setReferredBy(referrer);
			 * referrer.setReferralLevel(determineReferralLevel(paymentDetails));
			 */

			userRepository.save(referredMember); // Update referred member
			userRepository.save(referrer); // Update referrer
		} else {
			throw new IllegalArgumentException("Referrer or referred member not found.");
		}
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
