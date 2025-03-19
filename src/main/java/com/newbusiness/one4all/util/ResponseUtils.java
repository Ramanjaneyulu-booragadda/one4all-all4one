package com.newbusiness.one4all.util;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.newbusiness.one4all.dto.PaymentDetailDTO;
import com.newbusiness.one4all.model.PaymentDetails;

import de.huxhorn.sulky.ulid.ULID;

public class ResponseUtils {
	
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
        dto.setOfaGivenAmount(paymentDetails.getOfaGivenAmount());
        dto.setOfaPaymentStatus(paymentDetails.getOfaPaymentStatus().name()); // Enum to String
        dto.setOfaMobile(paymentDetails.getOfaMobile());
        
        return dto;
    }
    
 // Extract roles from the JWT token
    public static Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getClaimAsStringList("roles").stream().collect(Collectors.toSet());
        }
        throw new SecurityException("Unable to fetch roles from token.");
    }

    // Check if the user has a specific role
    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    // Check if the user has any of the provided roles
    public static boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    public static ApiResponse buildErrorResponse(String status, int code, String descriptionKey) {
        String correlationId = generateCorrelationID();
        String transactionDate = getCurrentTimestamp();

        String description = GlobalConstants.ERROR_MESSAGES.getOrDefault(descriptionKey, "An unknown error occurred.");

        Map<String, Object> errorResponse = Map.of(
                "status", status,
                "code", code,
                "description", description
        );

        return new ApiResponse(correlationId, transactionDate, status, List.of(errorResponse), null);  // Matches the first constructor
    }


}

