package com.newbusiness.one4all.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.dto.MemberProfileResponse;
import com.newbusiness.one4all.dto.UnassignedMemberToReffererSystemDto;
import com.newbusiness.one4all.dto.UpdateProfileRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;
import com.newbusiness.one4all.security.RoleCheck;
import com.newbusiness.one4all.service.EmailService;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.service.PasswordResetService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

	@Autowired private MemberService userService;
	@Autowired private PasswordResetService resetService;
	@Autowired private JwtEncoder jwtEncoder;
	@Autowired private JwtDecoder jwtDecoder;
	@Autowired private EmailService emailService;

	@Value("${microservice.url}")
	private String microServiceUrl;
	
	@Value("${frontend.reset-password.base-url}")
	private String resetPasswordBaseUrl;

	// 🚀 Public User Registration
	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@RequestHeader("Client-Authorization") String clientToken,
	                                      @Valid @RequestBody Member user,
	                                      @RequestHeader(required = false) Set<String> roles,
	                                      BindingResult result) {

		log.info("📩 Registering new user: {}", user.getOfaEmail());

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
			// Send registration email with member details
			emailService.sendRegistrationEmail(registeredUser.getOfaEmail(), registeredUser.getOfaFullName(), data);
			return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("status", "Success", "message", "User Registered Successfully", "RegistrationDetails", data)
			)));

		} catch (Exception e) {
			log.error("❌ Registration error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
		}
	}

	// ✅ Admin Registration
	@PostMapping(value = "/admin/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerAdmin(@RequestHeader("Client-Authorization") String clientToken,
	                                       @Valid @RequestBody Member user,
	                                       @RequestHeader Set<String> roles,
	                                       BindingResult result) {

		log.info("🛡 Admin registration request: {}", user.getOfaEmail());

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
			// Send registration email for admin with member details
			emailService.sendRegistrationEmail(registeredAdmin.getOfaEmail(), registeredAdmin.getOfaFullName(), data);
			return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("status", "Success", "message", "Admin Registered Successfully", "RegistrationDetails", data)
			)));

		} catch (Exception e) {
			log.error("❌ Admin registration failed", e);
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
			String resetLink = resetPasswordBaseUrl + token;
			log.info("🔗 Password Reset Link: {}", resetLink);
			// Send password reset email
			emailService.sendPasswordResetEmail(email, resetLink);
		}
		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(Map.of("message", "Password reset link sent to your Registered email. please check and reset password."))));
	}

	// ✅ Password Reset: Confirm
	@PostMapping("/reset-password/confirm")
	public ResponseEntity<?> confirmReset(@RequestBody Map<String, String> request) {
		String token = request.get("token");
		boolean success = resetService.resetPassword(token, request.get("newPassword"));
		if (success) {
			// Fetch the email from the token using the service method
			resetService.getEmailByToken(token).ifPresent(emailService::sendPasswordResetSuccessEmail);
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
	
	@GetMapping("/member/profile/{memberId}")
	public ResponseEntity<ApiResponse<Object>> getMemberProfile(@PathVariable String memberId) {
		log.info("inside member profile method");
		MemberProfileResponse profile=userService.getMemberProfile(memberId);
		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("statusMessage", GlobalConstants.PROFILEDETAILS_SUCCESS_MSZ, "status", HttpStatus.FOUND,
				       "member", List.of(profile))
			)));
	}
	@PutMapping("/member/profile/{memberId}")
	public ResponseEntity<ApiResponse<Object>> updateMemberProfile(
	        @PathVariable String memberId,
	        @Valid @RequestBody UpdateProfileRequest request) {
		log.info("inside updateMemberProfile method");
		MemberProfileResponse profile=userService.updateMemberProfile(memberId, request);
		return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("statusMessage", GlobalConstants.PROFILEDETAILS_UPDTAE_SUCCESS_MSZ, "status", HttpStatus.CREATED,
				       "member", List.of(profile))
			)));
	}
	 
	@GetMapping("/unassigned-members")
	@RoleCheck({GlobalConstants.ROLE_ADMIN_RW})
	public ResponseEntity<ApiResponse<Object>> getUnassignedMembers(@RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<UnassignedMemberToReffererSystemDto> unassignedMembers = userService.getUnassignedMembers(pageable);
	    return ResponseEntity.ok(ResponseUtils.buildApiResponse(List.of(
				Map.of("statusMessage", GlobalConstants.UNASSIGNED_MEMBERS_DETAILS, "status", HttpStatus.FOUND,
				       "member", unassignedMembers.getContent(),"UnassignedMemberCount",unassignedMembers.getTotalElements(),"currentPage", unassignedMembers.getNumber(),
				       "totalPages", unassignedMembers.getTotalPages())
			)));
	    
	    
	}
}
