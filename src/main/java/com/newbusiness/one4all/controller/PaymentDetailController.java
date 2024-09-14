package com.newbusiness.one4all.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.service.PaymentDetailService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentDetailController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentDetailController.class);
	@Autowired
	private PaymentDetailService paymentDetailService;

	// CREATE
	@PostMapping("/addpayment")
	public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDetails paymentDetails, BindingResult result) {
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
			PaymentDetails paymentDetail = paymentDetailService.addPayment(paymentDetails);
			ApiResponse apiResponse = ResponseUtils.buildApiResponse(
					Collections.singletonList(Map.of("status", GlobalConstants.PAYMENT_CREATION_SUCCESS, "errorCode",
							HttpStatus.CREATED, "message", Collections.singletonList(paymentDetail))));
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			logger.error("Error while registering user", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while registering user");
		}

	}

	// READ
	@GetMapping("/{id}")
	public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
		logger.info("Get payment request received for ID: {}", id);
		Optional<PaymentDetails> paymentDetail = paymentDetailService.getPaymentById(id);
		if (!paymentDetail.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GlobalConstants.PAYMENT_NOT_FOUND);
		}
		ApiResponse apiResponse = ResponseUtils.buildApiResponse(Collections.singletonList(Map.of("status",
				GlobalConstants.PAYMENT_RETRIEVAL_SUCCESS, "message", Collections.singletonList(paymentDetail.get()))));
		return ResponseEntity.ok(apiResponse);
	}

	// UPDATE
	@PutMapping("/updatepayment/{id}")
	public ResponseEntity<?> updatePayment(@PathVariable Long id,
			@Valid @RequestBody PaymentDetailDTO paymentDetailDTO) {
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

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePayment(@PathVariable Long id) {
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

	// READ
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
	        paymentMap.put("ofaContactNumber", payment.getOfaContactNumber()); // String
	        paymentMap.put("ofaStageNo", payment.getOfaStageNo()); // Integer
	        paymentMap.put("ofaPaymentStatus", payment.getOfaPaymentStatus()); // Enum (OfaPaymentStatus)
	        paymentMap.put("ofaCountdown", payment.getOfaCountdown()); // Integer
	        paymentMap.put("ofaCreatedAt", payment.getOfaCreatedAt()); // LocalDateTime
	        paymentMap.put("ofaUpdatedAt", payment.getOfaUpdatedAt()); // LocalDateTime

	        return paymentMap;
	    }).collect(Collectors.toList());


	    
	    ApiResponse apiResponse = ResponseUtils.buildApiResponse(Collections.singletonList(Map.of(
	        "status", GlobalConstants.PAYMENT_RETRIEVAL_SUCCESS,
	        "message", messages,
	        "errorCode", "OK"
	    )));
	    
	    return ResponseEntity.ok(apiResponse);
	}

}
