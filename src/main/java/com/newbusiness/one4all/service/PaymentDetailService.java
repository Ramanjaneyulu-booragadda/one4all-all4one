package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UserRepository;

@Service
public class PaymentDetailService {
	@Autowired
	private  PaymentDetailRepository paymentDetailRepository;

	public PaymentDetails addPayment(PaymentDetails paymentDetails) {
		// Here you can implement other registration logic, like encrypting the password
		
		return paymentDetailRepository.save(paymentDetails);
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
}
