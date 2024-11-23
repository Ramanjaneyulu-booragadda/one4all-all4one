package com.newbusiness.one4all.util;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.model.PaymentDetails;

import de.huxhorn.sulky.ulid.ULID;

public class ResponseUtils {
	@Autowired
	private Environment environment;
    public static String generateCorrelationID() {
    	ULID ulid = new ULID();
	    return ulid.nextULID();
    }

    public static String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(java.time.LocalDateTime.now());
    }

    public static ApiResponse buildApiResponse( List<Map<String, Object>> messages) {
    	String correlationId=generateCorrelationID(); 
    	String transactionDate=getCurrentTimestamp();
        return new ApiResponse(correlationId, transactionDate, messages);
    }

    public static Map<String, Object> createMessage(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Keys and values must be in pairs");
        }
        Map<String, Object> message = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            message.put(keyValues[i].toString(), keyValues[i + 1]);
        }
        return message;
    }
 // Utility method to generate a custom ID
    public static String generateCustomId(String idPrefix,int numberLength) {
        Random random = new Random();
        int randomNumber = (int) (Math.pow(10, numberLength - 1) + random.nextInt((int) (Math.pow(10, numberLength) - Math.pow(10, numberLength - 1))));
        return idPrefix + randomNumber; // Concatenates the prefix with the random number
    }
    public static ApiResponse buildValidationErrorResponse(List<Map<String, Object>> errorMessages) {
        String correlationId = generateCorrelationID();
        String transactionDate = getCurrentTimestamp();
        Map<String, Object> errorResponse = Map.of(
            "status", "Validation Error from server side",
            "validationmMessage", errorMessages,
            "code", 400
        );
        return new ApiResponse(correlationId, transactionDate, Collections.singletonList(errorResponse));
    }
    public static PaymentDetailDTO convertToDTO(PaymentDetails paymentDetails) {
        PaymentDetailDTO dto = new PaymentDetailDTO();
        dto.setOfaConsumerName(paymentDetails.getOfaConsumerName());
        dto.setOfaConsumerNo(paymentDetails.getOfaConsumerNo());
        dto.setOfaHelpAmount(paymentDetails.getOfaHelpAmount());
        dto.setOfaPaymentStatus(paymentDetails.getOfaPaymentStatus().name()); // Enum to String
        dto.setOfaStageNo(paymentDetails.getOfaStageNo());
        dto.setOfaRefferalAmount(paymentDetails.getOfaRefferalAmount());
        dto.setOfaMobile(paymentDetails.getOfaMobile());
        dto.setOfaParentConsumerNo(paymentDetails.getOfaParentConsumerNo());
        return dto;
    }
    
	
}

