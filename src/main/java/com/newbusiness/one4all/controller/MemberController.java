package com.newbusiness.one4all.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class MemberController {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	@Autowired
	private MemberService userService;

	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@Valid @RequestBody Member user, BindingResult result) {
		logger.info("request received with the user" + user);
		if (result.hasErrors()) {
			 List<Map<String, Object>> errorMessages = result.getAllErrors().stream()
			            .map(error -> {
			                Map<String, Object> errorMap = new HashMap<>();
			                errorMap.put("field", ((FieldError) error).getField());
			                errorMap.put("message", error.getDefaultMessage());
			                errorMap.put("rejectedValue", ((FieldError) error).getRejectedValue());
			                return errorMap;
			            })
			            .collect(Collectors.toList());
			        ApiResponse apiResponse = ResponseUtils.buildValidationErrorResponse(errorMessages);
			        return ResponseEntity.badRequest().body(apiResponse);
		}
		try {
			Member registeredUser = userService.registerNewMember(user);
			Map<String, Object> registrationDetails = Map.of("MemberID", registeredUser.getOfaMemberId(), "emailid",
					registeredUser.getOfaEmail(), "Mobile", registeredUser.getOfaMobileNo());

			Map<String, Object> message = Map.of("status", "Success", "code", 200, "message",
					"Record created successfully.", "RegistrationDetails", registrationDetails);
			List<Map<String, Object>> messages = List.of(message);
			ApiResponse apiResponse = ResponseUtils.buildApiResponse(messages);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			logger.error("Error while registering user", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while registering user");
		}

	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		Optional<Member> validUser = userService.validateLogin(loginRequest);
		if (!validUser.isPresent()) {
			return ResponseEntity.badRequest().body(GlobalConstants.USER_LOGIN_FAILED);
		}
		/*
		 * //return ResponseEntity.ok().body("Login successful."); ApiResponse
		 * apiResponse = ResponseUtils.buildApiResponse(Collections.singletonList(
		 * Map.of( "statusMessage", GlobalConstants.USER_LOGIN_SUCCESS, "status",
		 * HttpStatus.OK, "member", Collections.singletonList(validUser) ) ));
		 */
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
