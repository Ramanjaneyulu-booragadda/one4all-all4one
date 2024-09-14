
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

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Map<String, Object>> messages = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> {
			Map<String, Object> error = new HashMap<>();
			error.put("status", "bad request");
			error.put("errorCode", 400);
			error.put("message", fieldError.getDefaultMessage());
			return error;
		}).collect(Collectors.toList());

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), messages);
		return new ResponseEntity<>(apiResponse, headers, HttpStatus.BAD_REQUEST);
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

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
		logger.error("Data Integrity Violation: ", ex);
		/*
		 * // You can provide a more specific message depending on the exact
		 * requirements String message = "Database error occurred"; if (ex.getCause()
		 * instanceof ConstraintViolationException) { message = "Database error: " +
		 * ex.getRootCause().getMessage(); } return
		 * ResponseEntity.status(HttpStatus.CONFLICT).body(message);
		 */

		List<Map<String, Object>> message = Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
				HttpStatus.INTERNAL_SERVER_ERROR, "message", Collections.singletonList(ex.getMessage())));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<Object> handlePersistenceException(PersistenceException ex, WebRequest request) {
		logger.error("Persistence error occurred: " + ex.getMessage());
		List<Map<String, Object>> message = Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
				HttpStatus.INTERNAL_SERVER_ERROR, "message", Collections.singletonList(ex.getMessage())));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
		logger.error("handleGlobalException: " + ex.getMessage());
		List<Map<String, Object>> message = Collections.singletonList(Map.of("status", "Internal Error", "errorCode",
				HttpStatus.INTERNAL_SERVER_ERROR, "message", Collections.singletonList(ex.getMessage())));

		ApiResponse apiResponse = new ApiResponse(generateCorrelationID(), getCurrentTimestamp(), message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

}
