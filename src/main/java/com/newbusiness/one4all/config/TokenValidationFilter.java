package com.newbusiness.one4all.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TokenValidationFilter extends OncePerRequestFilter {

	private final JwtDecoder jwtDecoder;
	private final ObjectMapper objectMapper;
	@Autowired
	private Environment env; // 🔥 Inject Environment to fetch clientID

	public TokenValidationFilter(JwtDecoder jwtDecoder, ObjectMapper objectMapper) {
		this.jwtDecoder = jwtDecoder;
		this.objectMapper = objectMapper;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest wrappedRequest = new ContentCachingRequestWrapper(request); // <-- wrapper
		log.info("➡️ HTTP Method: {}", request.getMethod());
		log.info("➡️ URI: {}", request.getRequestURI());
		log.info("➡️ Headers:");
		Collections.list(request.getHeaderNames()).forEach(h -> log.info("     {}: {}", h, wrappedRequest.getHeader(h)));

		String requestUri = wrappedRequest.getRequestURI();
		log.info("Processing request: {}", requestUri);
		
		if (requestUri.equals("/api/login") || requestUri.equals("/api/register")
				||requestUri.equals("/api/admin/login") || requestUri.equals("/api/admin/register")
				 || requestUri.equals("/api/admin/reset-password-request") || requestUri.equals("/api/reset-password-request")) {
			filterChain.doFilter(wrappedRequest, response);
			return;
		}

		String clientToken = wrappedRequest.getHeader("Client-Authorization");
		String userToken = wrappedRequest.getHeader("Authorization");

		if (!isValidClientToken(clientToken) || !isValidUserToken(userToken)) {
			sendErrorResponse(response, "Invalid or missing authentication tokens",
					HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
		String tokenMemberId = jwt.getClaim("ofaMemberId");
		String requestMemberId = extractMemberId(wrappedRequest);

		if (requestMemberId != null && !requestMemberId.equals(tokenMemberId)) {
			sendErrorResponse(response, "Member ID in token does not match request", HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isValidClientToken(String clientToken) {
		try {
			if (clientToken == null) {
				log.error("Client token is missing.");
				return false;
			}

			Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));

			// Fetch clientID dynamically from Environmenta
			String expectedClientID = env.getProperty("microservice.clientid");
			// Fetch 'aud' claim as a List
			List<String> audienceList = jwt.getClaimAsStringList("aud");

			// Check if audience contains expectedClientID
			boolean isValidAudience = audienceList != null && audienceList.contains(expectedClientID);

			log.info("🟢 JWT 'aud' claim: {}", audienceList);
			log.info("🟢 Expected ClientID: {}", expectedClientID);

			return isValidAudience;
		} catch (JwtException e) {
			log.error("JWT validation failed: {}", e.getMessage());
			return false;
		}
	}

	private boolean isValidUserToken(String userToken) {
		try {
			if (userToken == null)
				return false;
			log.info("🛡️  Raw User Token: {}", userToken);

			Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
			log.info("🪪 Decoded User JWT Header: {}", jwt.getHeaders());
			log.info("🧾 Decoded User JWT Claims: {}", jwt.getClaims());
			log.info("✅ Token successfully decoded");
			return jwt.getClaim("roles") != null;
		} catch (JwtException e) {
			log.error("❌ User JWT decoding failed: {}", e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private String extractMemberId(HttpServletRequest request) throws IOException {
		// Check Headers
		String memberId = request.getHeader("ofaMemberId");
		if (memberId != null)
			return memberId;

		// Check Query Parameters
		memberId = request.getParameter("ofaMemberId");
		if (memberId != null)
			return memberId;

		// Check Request Body (for POST, PUT, etc.)
		// Try body safely from cached request
	    if (request instanceof ContentCachingRequestWrapper) {
	        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
	        byte[] buf = wrapper.getContentAsByteArray();
	        if (buf.length > 0) {
	            Map<String, Object> body = objectMapper.readValue(buf, Map.class);
	            return (String) body.get("ofaMemberId");
	        }
	    }

		// Extract from Path
		return extractMemberIdFromPath(request);
	}

	private String extractMemberIdFromPath(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String[] parts = uri.split("/");
		for (String part : parts) {
			if (part.startsWith("O4AA4O")) {
				return part;
			}
		}
		return null;
	}

	private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
		ApiResponse errorResponse = ResponseUtils.buildErrorResponse("Unauthorized", status, message);
		response.setStatus(status);
		response.setContentType("application/json");
		response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
	}

}
