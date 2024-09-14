package com.newbusiness.one4all.util;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ResponseUtils {

    public static String generateCorrelationID() {
        return UUID.randomUUID().toString();
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
}

