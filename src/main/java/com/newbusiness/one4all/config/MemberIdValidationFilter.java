package com.newbusiness.one4all.config;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class MemberIdValidationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;
    @Value("${microservice.clientid}")
    private String clientID;
    public MemberIdValidationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientToken = request.getHeader("Client-Authorization");
        String userToken = request.getHeader("Authorization");  // User token in Authorization header

        // Validate client token
        if (clientToken == null || !isValidClientToken(clientToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or missing client token.");
            return;
        }

        // Validate user token and member ID
        if (userToken == null || !isValidUserToken(userToken)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid or missing user token.");
            return;
        }

        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        String tokenMemberId = jwt.getClaim("ofaMemberId");
        String requestMemberId = extractMemberId(request);

        if (requestMemberId == null || !requestMemberId.equals(tokenMemberId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token does not match the memberId in the request.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Check if the client token is valid
    private boolean isValidClientToken(String clientToken) {
        try {
            Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));
            return jwt.getClaim("aud").equals(clientID) ;  // Check if the token contains a client_id claim
        } catch (JwtException e) {
            return false;
        }
    }

    // Check if the user token is valid
    private boolean isValidUserToken(String userToken) {
        try {
            Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
            return jwt.getClaim("roles") != null;  // Check if the token contains roles
        } catch (JwtException e) {
            return false;
        }
    }

    private String extractMemberId(HttpServletRequest request) throws IOException {
        // 1. Check Headers
        String memberId = request.getHeader("ofaMemberId");
        if (memberId != null) {
            return memberId;
        }

        // 2. Check Query Parameters
        memberId = request.getParameter("ofaMemberId");
        if (memberId != null) {
            return memberId;
        }

        // 3. Check Request Body (for POST, PUT, etc.)
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            Map<String, Object> body = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            if (body.containsKey("ofaMemberId")) {
                return (String) body.get("ofaMemberId");
            }
        }

        // 4. Check if the URI contains the memberId
        return extractMemberIdFromPath(request);
    }

    private String extractMemberIdFromPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String[] parts = uri.split("/");
        for (String part : parts) {
            if (part.contains("O4AA4O")) {
                return part;  // Return the memberId from the URI
            }
        }
        return null;
    }
}

