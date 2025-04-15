package com.newbusiness.one4all.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.service.PasswordResetService;
import com.newbusiness.one4all.util.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class MemberController {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired private MemberService userService;
	@Autowired private PasswordResetService resetService;
	@Autowired private JwtEncoder jwtEncoder;
	@Autowired private JwtDecoder jwtDecoder;

	@Value("${microservice.url}")
	private String microServiceUrl;

	// 🚀 Public User Registration
	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@RequestHeader("Client-Authorization") String clientToken,
	                                      @Valid @RequestBody Member user,
	                                      @RequestHeader(required = false) Set<String> roles,
	                                      BindingResult result) {

		logger.info("📩 Registering new user: {}", user.getOfaEmail());

		if (!SecurityUtils.isValidClientToken(clientToken, jwtDecoder)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client token");
		}

		if (roles == null || roles.isEmpty()) {
			roles = Set.of("ONE4ALL_USER_RO");
		}

		if (roles.stream().anyMatch(r -> r.contains("ADMIN"))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ResponseUtils.buildErrorResponse("Invalid Role", 403, "Admin roles not allowed in public registration."));
		}

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(
				ResponseUtils.buildValidationErrorResponse(result.getFieldErrors().stream().map(error -> {
					Map<String, Object> map = new HashMap<>();
					map.put("field", error.getField());
					map.put("message", error.getDefaultMessage());
					return map;
				}).toList())
			);
		}

		try {
			Member registeredUser = userService.registerNewMember(user, roles);
			Map<String, Object> data = Map.of(
				"MemberID", registeredUser.getOfaMemberId(),
				"emailid", registeredUser.getOfaEmail(),
				"Mobile", registeredUser.getOfaMobileNo()
			);

			return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("status", "Success", "message", "User Registered Successfully", "RegistrationDetails", data)
			)));

		} catch (Exception e) {
			logger.error("❌ Registration error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
		}
	}

	// ✅ Admin Registration
	@PostMapping(value = "/admin/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerAdmin(@RequestHeader("Client-Authorization") String clientToken,
	                                       @Valid @RequestBody Member user,
	                                       @RequestParam Set<String> roles,
	                                       BindingResult result) {

		logger.info("🛡 Admin registration request: {}", user.getOfaEmail());

		if (!SecurityUtils.isValidClientToken(clientToken, jwtDecoder)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client token");
		}

		if (roles == null || roles.isEmpty() || !roles.stream().allMatch(r -> r.startsWith("ONE4ALL_ADMIN_"))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN roles are allowed.");
		}

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(ResponseUtils.buildValidationErrorResponse(
				result.getFieldErrors().stream().map(error -> {
					Map<String, Object> map = new HashMap<>();
					map.put("field", error.getField());
					map.put("message", error.getDefaultMessage());
					return map;
				}).toList())
			);
		}

		try {
			Member registeredAdmin = userService.registerNewMember(user, roles);
			Map<String, Object> data = Map.of(
				"MemberID", registeredAdmin.getOfaMemberId(),
				"emailid", registeredAdmin.getOfaEmail(),
				"Mobile", registeredAdmin.getOfaMobileNo()
			);

			return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("status", "Success", "message", "Admin Registered Successfully", "RegistrationDetails", data)
			)));

		} catch (Exception e) {
			logger.error("❌ Admin registration failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Admin registration failed.");
		}
	}

	// ✅ Login (Common for user)
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		Optional<Member> validUser = userService.validateLogin(loginRequest);
		if (validUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ResponseUtils.buildErrorResponse("Authentication Failed", 401, GlobalConstants.USER_LOGIN_FAILED));
		}

		Member user = validUser.get();
		String token = generateToken(user);

		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
			Map.of("statusMessage", GlobalConstants.USER_LOGIN_SUCCESS, "status", HttpStatus.OK,
			       "member", List.of(user), "token", token)
		)));
	}

	// ✅ Admin Login
	@PostMapping("/admin/login")
	public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
		Optional<Member> validUser = userService.validateLogin(loginRequest);
		if (validUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ResponseUtils.buildErrorResponse("Authentication Failed", 401, GlobalConstants.USER_LOGIN_FAILED));
		}

		Member user = validUser.get();
		boolean isAdmin = user.getRoles().stream()
			.map(Role::getRoleName)
			.anyMatch(role -> role.startsWith("ONE4ALL_ADMIN_"));

		if (!isAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Not an admin.");
		}

		String token = generateToken(user);

		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
			Map.of("statusMessage", "Admin Login successful.", "status", HttpStatus.OK,
			       "member", List.of(user), "token", token)
		)));
	}

	// ✅ Password Reset: Request
	@PostMapping("/reset-password-request")
	public ResponseEntity<?> requestReset(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String token = resetService.createResetToken(email);
		if (token != null) {
			String resetLink = "http://localhost:3000/reset-password/confirm/" + token;
			logger.info("🔗 Password Reset Link: {}", resetLink); // You can send this via email
		}
		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(Map.of("message", "If email exists, reset link has been sent."))));
	}

	// ✅ Password Reset: Confirm
	@PostMapping("/reset-password/confirm")
	public ResponseEntity<?> confirmReset(@RequestBody Map<String, String> request) {
		boolean success = resetService.resetPassword(request.get("token"), request.get("newPassword"));
		if (success) {
			return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(Map.of("message", "Password reset successful."))));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ResponseUtils.buildErrorResponse("Reset Failed", 400, "Invalid or expired token"));
	}

	// 📦 Reusable JWT generation logic
	private String generateToken(Member user) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer(microServiceUrl)
			.issuedAt(now)
			.expiresAt(now.plus(1, ChronoUnit.HOURS))
			.subject(user.getOfaMemberId())
			.claim("ofaMemberId", user.getOfaMemberId())
			.claim("roles", Optional.ofNullable(user.getRoles()).orElse(Set.of()).stream().map(Role::getRoleName).toList())
			.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
}
