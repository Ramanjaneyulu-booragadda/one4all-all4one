
package com.newbusiness.one4all.exception;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.newbusiness.one4all.util.ApiResponse;

import jakarta.persistence.PersistenceException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this handler is high priority if multiple handlers exist
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private String generateCorrelationID() {
		return UUID.randomUUID().toString();
	}

	private String getCurrentTimestamp() {
		return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(java.time.LocalDateTime.now());
	}

	// You can also add specific handlers for specific types of exceptions
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		logger.error("handleIllegalArgumentException: ", ex);
		List<Map<String, Object>> message = Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
				HttpStatus.BAD_REQUEST, "message", Collections.singletonList(ex.getMessage())));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}

	/*
	 * @ExceptionHandler(DataIntegrityViolationException.class) public
	 * ResponseEntity<Object>
	 * handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest
	 * request) { logger.error("Data Integrity Violation: ", ex);
	 * 
	 * // You can provide a more specific message depending on the exact
	 * requirements String message = "Database error occurred"; if (ex.getCause()
	 * instanceof ConstraintViolationException) { message = "Database error: " +
	 * ex.getRootCause().getMessage(); } return
	 * ResponseEntity.status(HttpStatus.CONFLICT).body(message);
	 * 
	 * 
	 * List<Map<String, Object>> message =
	 * Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
	 * HttpStatus.INTERNAL_SERVER_ERROR, "message",
	 * Collections.singletonList(ex.getMessage())));
	 * 
	 * ApiResponse apiResponse = new ApiResponse(generateCorrelationID(),
	 * getCurrentTimestamp(), message); return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse); }
	 */

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> {
			Map<String, Object> error = new HashMap<>();
			error.put("field", fieldError.getField());
			error.put("message", fieldError.getDefaultMessage());
			error.put("rejectedValue", fieldError.getRejectedValue());
			error.put("errorCode", HttpStatus.BAD_REQUEST.value());
			return error;
		}).collect(Collectors.toList());

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), errors);
		return new ResponseEntity<>(apiResponse, headers, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle IllegalStateException for specific business logic issues
	 */
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
		logger.error("IllegalStateException caught in GlobalExceptionHandler: ", ex);

		List<Map<String, Object>> error = Collections.singletonList(Map.of("status", "Referrer Limit Exceeded",
				"errorCode", HttpStatus.BAD_REQUEST.value(), "message", ex.getMessage()));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), error);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}

	/**
	 * Handle DataIntegrityViolationException for database-related issues
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {
		logger.error("Data Integrity Violation caught: ", ex);

		String message = "Database error: "
				+ (ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Unknown database error");
		List<Map<String, Object>> error = Collections.singletonList(
				Map.of("status", "Conflict", "errorCode", HttpStatus.CONFLICT.value(), "message", message));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), error);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
	}

	/**
	 * Handle PersistenceException for general database-related issues
	 */
	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<Object> handlePersistenceException(PersistenceException ex, WebRequest request) {
		logger.error("PersistenceException caught: ", ex);

		List<Map<String, Object>> error = Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
				HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", ex.getMessage()));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), error);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

	/**
	 * Handle general exceptions that are not caught by specific handlers
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
		logger.error("Global exception caught: ", ex);

		List<Map<String, Object>> error = Collections.singletonList(Map.of("status", "Internal Server Error",
				"errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value(), "message", ex.getMessage()));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), error);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}
}
