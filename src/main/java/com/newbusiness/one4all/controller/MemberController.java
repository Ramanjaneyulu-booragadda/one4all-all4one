package com.newbusiness.one4all.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class MemberController {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	@Autowired
	private MemberService userService;


	 @Autowired
	    private JwtEncoder jwtEncoder;
	 

	    @Autowired
	    private JwtDecoder jwtDecoder;
	@Value("${microservice.url}")
    private String microServiceUrl;

	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@RequestHeader("Client-Authorization") String clientToken,
	        @RequestHeader("Authorization") String userToken,@Valid @RequestBody Member user, @RequestParam Set<String> roles,BindingResult result) {
		logger.info("request received with the user" + user);
		// Validate tokens
        if (!SecurityUtils.isValidClientToken(clientToken, jwtDecoder)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing client token.");
        }

        if (!SecurityUtils.isValidUserToken(userToken, jwtDecoder) ||
            !SecurityUtils.hasPermissionForAction(userToken, jwtDecoder, "WRITE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to register users.");
        }
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
			Member registeredUser = userService.registerNewMember(user,roles);
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
        	ApiResponse errorResponse = ResponseUtils.buildErrorResponse(
                    "Authentication Failed", 401, GlobalConstants.USER_LOGIN_FAILED
            );
            return ResponseEntity.badRequest().body(GlobalConstants.USER_LOGIN_FAILED);
        }


        Member user = validUser.get();

        // Generate JWT for authenticated user
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(microServiceUrl)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(user.getOfaMemberId())
                .claim("ofaMemberId", user.getOfaMemberId())
                .claim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();



		ApiResponse apiResponse = ResponseUtils.buildApiResponse(
				Collections.singletonList(Map.of("statusMessage", GlobalConstants.USER_LOGIN_SUCCESS, "status",
						HttpStatus.OK, "member", Collections.singletonList(validUser), "token", accessToken)));
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping(value = "/bulk-register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> bulkRegisterUsers(@RequestHeader("Client-Authorization") String clientToken,
            @RequestHeader("Authorization") String userToken,@Valid @RequestBody List<Member> users, BindingResult result) {
		logger.info("Bulk registration request received with {} users", users.size());
		if (!SecurityUtils.isValidClientToken(clientToken, jwtDecoder)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing client token.");
        }

        if (!SecurityUtils.isValidUserToken(userToken, jwtDecoder) ||
            !SecurityUtils.hasPermissionForAction(userToken, jwtDecoder, "WRITE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to bulk register users.");
        }
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
			List<Member> registeredUsers = userService.bulkRegisterMembers(users);

			List<Map<String, Object>> registrationDetails = registeredUsers.stream().map(registeredUser -> {
				Map<String, Object> userDetails = new HashMap<>();
				userDetails.put("MemberID", registeredUser.getOfaMemberId());
				userDetails.put("emailid", registeredUser.getOfaEmail());
				userDetails.put("Mobile", registeredUser.getOfaMobileNo());
				return userDetails;
			}).collect(Collectors.toList());

			Map<String, Object> message = new HashMap<>();
			message.put("status", "Success");
			message.put("code", 200);
			message.put("message", "Records created successfully.");
			message.put("RegistrationDetails", registrationDetails);

			List<Map<String, Object>> messages = List.of(message);
			ApiResponse apiResponse = ResponseUtils.buildApiResponse(messages);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			logger.error("Error while registering users in bulk", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error while registering users in bulk");
		}
	}

}
