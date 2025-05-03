package com.newbusiness.one4all.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


import de.huxhorn.sulky.ulid.ULID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
public class ResponseUtils {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static String generateCorrelationID() {
        ULID ulid = new ULID();
        return ulid.nextULID();
    }

    public static String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(java.time.LocalDateTime.now());
    }

    // ✅ Generic success
    public static ApiResponse buildApiResponse(List<Map<String, Object>> messages) {
        return new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), messages);
    }

    // ✅ Refined for clear success format
    public static ApiResponse buildSuccessResponse(Object data, String messageText) {
        Map<String, Object> successMessage = Map.of(
            "status", "OK",
            "statusMessage", messageText,
            "data", data
        );
        return new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), List.of(successMessage));
    }

    // ✅ Refined error format
    public static ApiResponse buildErrorResponse(String status, int code, String descriptionKey) {
        String description = GlobalConstants.ERROR_MESSAGES.getOrDefault(descriptionKey, "Unexpected error.");

        Map<String, Object> errorDetails = Map.of(
            "code", code,
            "description", description
        );

        Map<String, Object> errorMessage = Map.of(
            "status", status,
            "statusMessage", description
        );

        return new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), "Error",
                List.of(errorMessage), errorDetails);
    }

    // ✅ Validation error response
    public static ApiResponse buildValidationErrorResponse(List<Map<String, Object>> fieldErrors) {
        Map<String, Object> errorMessage = Map.of(
            "status", "BAD_REQUEST",
            "statusMessage", "Validation Failed"
        );

        Map<String, Object> errorDetails = Map.of(
            "code", 400,
            "fieldErrors", fieldErrors
        );

        return new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), "Error",
                List.of(errorMessage), errorDetails);
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

    public static String generateCustomId(String idPrefix, int numberLength) {
        Random random = new Random();
        int randomNumber = (int) (Math.pow(10, numberLength - 1) + random.nextInt((int) Math.pow(10, numberLength - 1) * 9));
        return idPrefix + randomNumber;
    }



    public static Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return new HashSet<>(jwt.getClaimAsStringList("roles"));
        }
        throw new SecurityException("Unable to fetch roles from token.");
    }

    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    public static boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }
    public static ApiResponse buildSuccessResponse(Object data) {
        return buildSuccessResponse(data, "Success");
    }
    public static ApiResponse<Object> buildSuccessResponse(String messageText) {
        String transactionID = generateCorrelationID();
        String transactionDate = LocalDateTime.now().format(FORMATTER);
        return new ApiResponse<>(transactionID, transactionDate, messageText);
    }
    public static ApiResponse<Object> buildSuccessResponse(List<Map<String, Object>> messages) {
        String transactionID = generateCorrelationID();
        String transactionDate = LocalDateTime.now().format(FORMATTER);
        return new ApiResponse<>(transactionID, transactionDate, messages);
    }
    public static ApiResponse<Object> buildErrorResponse(String errorMessage, int errorCode) {
        String transactionID = generateCorrelationID();
        String transactionDate = LocalDateTime.now().format(FORMATTER);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", errorCode);
        errorDetails.put("message", errorMessage);

        return new ApiResponse<>(transactionID, transactionDate, "Error", Collections.emptyList(), errorDetails);
    }
    public static ResponseEntity<ApiResponse> buildUnauthorizedResponse(String message) {
        Map<String, Object> error = Map.of(
            "status", "UNAUTHORIZED",
            "statusMessage", message
        );
        ApiResponse response = new ApiResponse(
            generateCorrelationID(),
            getCurrentTimestamp(),
            "Error",
            List.of(error),
            Map.of("code", 401, "description", message)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
 // ✅ Simple success with plain messageText
    public static ApiResponse buildPlainSuccess(String messageText) {
        ApiResponse response = new ApiResponse();
        response.setTransactionID(generateCorrelationID());
        response.setTransactionDate(getCurrentTimestamp());
        response.setStatus("Success");
        response.setMessageText(messageText);
        return response;
    }

    // ✅ Simple error with plain messageText
    public static ApiResponse buildPlainError(String messageText, int statusCode) {
        ApiResponse response = new ApiResponse();
        response.setTransactionID(generateCorrelationID());
        response.setTransactionDate(getCurrentTimestamp());
        response.setStatus("Error");
        response.setMessageText(messageText);
        response.setErrorDetails(Map.of("code", statusCode));
        return response;
    }

}
