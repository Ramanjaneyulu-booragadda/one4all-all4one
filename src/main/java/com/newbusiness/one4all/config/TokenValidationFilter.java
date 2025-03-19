package com.newbusiness.one4all.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@Component
public class TokenValidationFilter extends OncePerRequestFilter {
	
    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(TokenValidationFilter.class);
    @Autowired
    private Environment env; // 🔥 Inject Environment to fetch clientID


    

    public TokenValidationFilter(JwtDecoder jwtDecoder, ObjectMapper objectMapper) {
        this.jwtDecoder = jwtDecoder;
        this.objectMapper = objectMapper;
        
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        logger.info("Processing request: {}", requestUri);

        // Skip validation for login and public endpoints
        if (requestUri.equals("/api/login") || requestUri.equals("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Retrieve tokens
        String clientToken = request.getHeader("Client-Authorization");
        String userToken = request.getHeader("Authorization");

        // Validate client and user tokens
        if (!isValidClientToken(clientToken) || !isValidUserToken(userToken)) {
            sendErrorResponse(response, "Invalid or missing authentication tokens", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Extract member ID from token
        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        String tokenMemberId = jwt.getClaim("ofaMemberId");
        String requestMemberId = extractMemberId(request);

        // Validate member ID if necessary
        if (requestMemberId != null && !requestMemberId.equals(tokenMemberId)) {
            sendErrorResponse(response, "Member ID in token does not match request", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

//    private boolean isValidClientToken(String clientToken) {
//        try {
//            if (clientToken == null) return false;
//            Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));
//            return clientID.equals(jwt.getClaim("aud"));
//        } catch (JwtException e) {
//            return false;
//        }
//    }
    private boolean isValidClientToken(String clientToken) {
        try {
            if (clientToken == null) {
                logger.error("Client token is missing.");
                return false;
            }

            Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));
            
         // Fetch clientID dynamically from Environmenta
            String expectedClientID = env.getProperty("microservice.clientid");
            // Fetch 'aud' claim as a List
            List<String> audienceList = jwt.getClaimAsStringList("aud");

            // Check if audience contains expectedClientID
            boolean isValidAudience = audienceList != null && audienceList.contains(expectedClientID);

            logger.info("🟢 JWT 'aud' claim: {}", audienceList);
            logger.info("🟢 Expected ClientID: {}", expectedClientID);


            return isValidAudience;
        } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
    private boolean isValidUserToken(String userToken) {
        try {
            if (userToken == null) return false;
            Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
            return jwt.getClaim("roles") != null;
        } catch (JwtException e) {
            return false;
        }
    }

    private String extractMemberId(HttpServletRequest request) throws IOException {
        // Check Headers
        String memberId = request.getHeader("ofaMemberId");
        if (memberId != null) return memberId;

        // Check Query Parameters
        memberId = request.getParameter("ofaMemberId");
        if (memberId != null) return memberId;

        // Check Request Body (for POST, PUT, etc.)
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            Map<String, Object> body = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            return (String) body.get("ofaMemberId");
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
