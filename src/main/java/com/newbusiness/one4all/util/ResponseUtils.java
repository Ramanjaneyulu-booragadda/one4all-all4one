package com.newbusiness.one4all.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import de.huxhorn.sulky.ulid.ULID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public static ApiResponse<Object> buildApiResponse(List<Map<String, Object>> messages) {
        log.info("Building API response with messages: {}", messages);
        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), messages);
    }

    // ✅ Refined for clear success format
    public static ApiResponse<Object> buildSuccessResponse(Object data, String messageText) {
        log.info("Building success response: messageText={}, data={}", messageText, data);
        Map<String, Object> successMessage = Map.of(
            "status", "OK",
            "statusMessage", messageText,
            "data", data
        );
        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(successMessage));
    }

    // ✅ Refined error format
    public static ApiResponse<Object> buildErrorResponse(String status, int code, String descriptionKey) {
        log.error("Building error response: status={}, code={}, descriptionKey={}", status, code, descriptionKey);
        String description = GlobalConstants.ERROR_MESSAGES.getOrDefault(descriptionKey, "Unexpected error.");

        Map<String, Object> errorDetails = Map.of(
            "code", code,
            "description", description
        );

        Map<String, Object> errorMessage = Map.of(
            "status", status,
            "errorDetails", errorDetails
        );

        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(errorMessage));
    }

    // Overloaded: Accepts direct error message instead of descriptionKey
    public static ApiResponse<Object> buildErrorResponseDirect(String status, int code, String description) {
        log.error("Building error response (direct): status={}, code={}, description={}", status, code, description);
        Map<String, Object> errorDetails = Map.of(
            "code", code,
            "description", description
        );
        Map<String, Object> errorMessage = Map.of(
            "status", status,
            "errorDetails", errorDetails
        );
        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(errorMessage));
    }

    public static ApiResponse<Object> buildValidationErrorResponse(List<Map<String, Object>> fieldErrors) {
        log.error("Building validation error response: {}", fieldErrors);
        Map<String, Object> errorMessage = Map.of(
            "status", "Validation Error",
            "fieldErrors", fieldErrors
        );
        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(errorMessage));
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
    public static ApiResponse<Object> buildSuccessResponse(Object data) {
        log.info("Building success response with data only: {}", data);
        return new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(Map.of("data", data)));
    }
    public static ResponseEntity<ApiResponse<Object>> buildUnauthorizedResponse(String message) {
        log.warn("Building unauthorized response: {}", message);
        ApiResponse<Object> response = new ApiResponse<>(generateCorrelationID(), getCurrentTimestamp(), List.of(Map.of("error", message)));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    public static ApiResponse<Object> buildPlainSuccess(String messageText) {
        log.info("Building plain success response: {}", messageText);
        ApiResponse<Object> response = new ApiResponse<>();
        response.setMessages(List.of(Map.of("message", messageText)));
        return response;
    }
    public static ApiResponse<Object> buildPlainError(String messageText, int statusCode) {
        log.error("Building plain error response: {}, statusCode={}", messageText, statusCode);
        ApiResponse<Object> response = new ApiResponse<>();
        response.setMessages(List.of(Map.of("error", messageText)));
        response.setErrorDetails(Map.of("code", statusCode));
        return response;
    }

}
