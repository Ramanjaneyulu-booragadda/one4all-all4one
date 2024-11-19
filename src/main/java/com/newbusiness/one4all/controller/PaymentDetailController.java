package com.newbusiness.one4all.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.service.MLMService;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.service.PaymentDetailService;
import com.newbusiness.one4all.service.ReferralService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.PaymentStatus;
import com.newbusiness.one4all.util.ResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")

public class PaymentDetailController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentDetailController.class);
	@Autowired
	private PaymentDetailService paymentDetailService;
	@Autowired
	private MLMService mlmService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ReferralService referralService;

	@PostMapping("/givehelp")
	public ResponseEntity<?> giveHelp(@Valid @RequestBody PaymentDetails paymentDetails, BindingResult result) {
		logger.info("Create payment request received: {}", paymentDetails);
		if (result.hasErrors()) {
			List<Map<String, Object>> errorMessages = result.getAllErrors().stream().map(error -> {
				Map<String, Object> errorMap = new HashMap<>();
				errorMap.put("field", ((FieldError) error).getField());
				errorMap.put("message", error.getDefaultMessage());
				errorMap.put("rejectedValue", ((FieldError) error).getRejectedValue());
				return errorMap;
			}).collect(Collectors.toList());
			ApiResponse apiResponse = ResponseUtils.buildValidationErrorResponse(errorMessages);
			return ResponseEntity.badRequest().body(apiResponse);
		}
		try {
			 // Check if the payment details already exist based on ofaConsumerNo
	        Optional<PaymentDetails> existingPayment = paymentDetailService.findByOfaConsumerNo(paymentDetails.getOfaConsumerNo());
	        if (existingPayment.isPresent()) {
	            // Return a message indicating the record already exists
	        	ApiResponse apiResponse = ResponseUtils.buildApiResponse(
		                Collections.singletonList(Map.of("status", GlobalConstants.DUPLICATE_PAYMENT_RECORD_FOUND + paymentDetails.getOfaConsumerNo(), "errorCode",
		                        HttpStatus.CONFLICT, "message", Collections.singletonList(existingPayment))));
	        	return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
	        } else {
	            // If no existing payment details, create a new one
	            //PaymentDetails paymentDetail = paymentDetailService.addPayment(paymentDetails);
	            // Pass paymentDetails to distributeHelp method
	        	PaymentDetails paymentDetail=distributeMoneyToUpliner(paymentDetails);

	            ApiResponse apiResponse = ResponseUtils.buildApiResponse(
	                Collections.singletonList(Map.of("status", GlobalConstants.PAYMENT_CREATION_SUCCESS, "errorCode",
	                        HttpStatus.CREATED, "message", Collections.singletonList(paymentDetail))));

	            return ResponseEntity.ok(apiResponse);
	        }
		} catch (Exception e) {
			logger.error("Error while registering user", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while registering user");
		}
	}

	
	public PaymentDetails distributeMoneyToUpliner(PaymentDetails paymentDetails) {
		 
		 BigDecimal refferalBonus=mlmService.getReferralBonusFromProperties();
		 BigDecimal remainingBalance = paymentDetails.getOfaHelpAmount(); // Start with full help amount
		 paymentDetails.setOfaTotalAmount(paymentDetails.getOfaHelpAmount());
		 // Pay direct referral bonus
		 Map<PaymentDetails,BigDecimal> objectWithBalance= mlmService.payDirectReferralBonus(paymentDetails, refferalBonus, remainingBalance);
		 if (!objectWithBalance.isEmpty()) {
	            Map.Entry<PaymentDetails, BigDecimal> entry = objectWithBalance.entrySet().iterator().next();
	            paymentDetails = entry.getKey();
	            remainingBalance = entry.getValue();
	        } 
		 // Get upliner details for the member
		List<UplinerWithMemberDetailsDTO> uplinerDetailsList = referralService
				.getUpliners(paymentDetails.getOfaConsumerNo());

		// Process upliners
        remainingBalance = mlmService.payUpliners(paymentDetails, uplinerDetailsList,remainingBalance);

        // Update remaining balance in the original paymentDetails
       // paymentDetails.setOfaHelpAmount(remainingBalance); 
        //return paymentDetailService.updatePayment(paymentDetails);
        return paymentDetails;
	}
	
	// READ (Get Help Status by ID)
	@GetMapping("/{id}")
	public ResponseEntity<?> getHelpStatusById(@PathVariable Long id) {
		logger.info("Get payment request received for ID: {}", id);
		Optional<PaymentDetails> paymentDetail = paymentDetailService.getPaymentById(id);
		if (!paymentDetail.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GlobalConstants.PAYMENT_NOT_FOUND);
		}
		ApiResponse apiResponse = ResponseUtils.buildApiResponse(Collections.singletonList(Map.of("status",
				GlobalConstants.PAYMENT_RETRIEVAL_SUCCESS, "message", Collections.singletonList(paymentDetail.get()))));
		return ResponseEntity.ok(apiResponse);
	}

	// UPDATE (Receive Help)
	@PutMapping("/receivehelp/{id}")
	public ResponseEntity<?> receiveHelp(@PathVariable Long id, @Valid @RequestBody PaymentDetailDTO paymentDetailDTO) {
		logger.info("Update payment request received for ID: {}: {}", id, paymentDetailDTO);
		Optional<PaymentDetails> updatedPaymentDetail = paymentDetailService.updatePayment(id, paymentDetailDTO);
		if (!updatedPaymentDetail.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GlobalConstants.PAYMENT_NOT_FOUND);
		}
		ApiResponse apiResponse = ResponseUtils
				.buildApiResponse(Collections.singletonList(Map.of("status", GlobalConstants.PAYMENT_UPDATE_SUCCESS,
						"message", Collections.singletonList(updatedPaymentDetail.get()))));
		return ResponseEntity.ok(apiResponse);
	}

	// DELETE (Cancel Help)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> cancelHelpRequest(@PathVariable Long id) {
		logger.info("Delete payment request received for ID: {}", id);
		boolean isDeleted = paymentDetailService.deletePayment(id);
		if (!isDeleted) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GlobalConstants.PAYMENT_NOT_FOUND);
		}
		ApiResponse apiResponse = ResponseUtils
				.buildApiResponse(Collections.singletonList(Map.of("status", GlobalConstants.PAYMENT_DELETION_SUCCESS,
						"message", "Payment with ID " + id + " was successfully deleted")));
		return ResponseEntity.ok(apiResponse);
	}

	// READ (Get all help requests)
	@GetMapping("/getall")
	public ResponseEntity<?> getAllPayments() {
		logger.info("Get all payment request ");
		List<PaymentDetails> paymentDetail = paymentDetailService.getAllPayments();

		if (paymentDetail.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GlobalConstants.PAYMENT_NOT_FOUND);
		}

		List<Map<String, Object>> messages = paymentDetail.stream().map(payment -> {
			Map<String, Object> paymentMap = new HashMap<>();

			// Adding different types of values to the map
			paymentMap.put("ofaPaymentId", payment.getOfaPaymentId()); // Long
			paymentMap.put("ofaConsumerNo", payment.getOfaConsumerNo()); // String
			paymentMap.put("ofaParentConsumerNo", payment.getOfaParentConsumerNo()); // String
			paymentMap.put("ofaConsumerName", payment.getOfaConsumerName()); // String
			paymentMap.put("ofaHelpAmount", payment.getOfaHelpAmount()); // BigDecimal
			paymentMap.put("ofaRefferalAmount", payment.getOfaRefferalAmount()); // BigDecimal
			paymentMap.put("ofaMobile", payment.getOfaMobile()); // String
			paymentMap.put("ofaRefferarNumber", payment.getOfaRefferarMobile()); // String
			paymentMap.put("ofaStageNo", payment.getOfaStageNo()); // Integer
			paymentMap.put("ofaPaymentStatus", payment.getOfaPaymentStatus()); // Enum (OfaPaymentStatus)
			paymentMap.put("ofaCountdown", payment.getOfaCountdown()); // Integer
			paymentMap.put("ofaCreatedAt", payment.getOfaCreatedAt()); // LocalDateTime
			paymentMap.put("ofaUpdatedAt", payment.getOfaUpdatedAt()); // LocalDateTime

			return paymentMap;
		}).collect(Collectors.toList());

		ApiResponse apiResponse = ResponseUtils.buildApiResponse(Collections.singletonList(
				Map.of("status", GlobalConstants.PAYMENT_RETRIEVAL_SUCCESS, "message", messages, "errorCode", "OK")));

		return ResponseEntity.ok(apiResponse);
	}

	
}