package com.newbusiness.one4all.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtils {

    public static boolean isValidClientToken(String clientToken, JwtDecoder jwtDecoder) {
        try {
            Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));
            log.info("Validated client token for client_id={}", String.valueOf(jwt.getClaim("client_id")));
            return jwt.getClaim("client_id") != null;
        } catch (Exception e) {
            log.error("Invalid client token: {}", clientToken, e);
            return false;
        }
    }

    public static boolean isValidUserToken(String userToken, JwtDecoder jwtDecoder) {
        try {
            Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
            log.info("Validated user token for roles={}", String.valueOf(jwt.getClaim("roles")));
            return jwt.getClaim("roles") != null;
        } catch (Exception e) {
            log.error("Invalid user token: {}", userToken, e);
            return false;
        }
    }

    // Check if the user has any of the required roles
    public static boolean hasRequiredRole(String userToken, JwtDecoder jwtDecoder, Set<String> requiredRoles) {
        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        Set<String> userRoles = jwt.getClaimAsStringList("roles").stream().collect(Collectors.toSet());
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }

    // Role hierarchy: for example, RW roles can perform both RO and W operations
    public static boolean hasPermissionForAction(String userToken, JwtDecoder jwtDecoder, String requiredPermission) {
        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        Set<String> userRoles = jwt.getClaimAsStringList("roles").stream().collect(Collectors.toSet());

        // Define role-based permission mappings
        Map<String, Set<String>> permissionMappings = Map.of(
            "READ", Set.of("ONE4ALL_ADMIN_RO", "ONE4ALL_ADMIN_RW", "ONE4ALL_USER_RO", "ONE4ALL_USER_RW"),
            "WRITE", Set.of("ONE4ALL_ADMIN_W", "ONE4ALL_ADMIN_RW", "ONE4ALL_USER_W", "ONE4ALL_USER_RW")
        );

        Set<String> rolesWithPermission = permissionMappings.getOrDefault(requiredPermission, Collections.emptySet());
        return userRoles.stream().anyMatch(rolesWithPermission::contains);
    }
    public static String getLoggedInMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("ofaMemberId"); // adjust the claim key if named differently
        }
        throw new RuntimeException("Unable to extract ofaMemberId from security context");
    }
    public static boolean isSpecialMember(String memberId) {
        return memberId != null && memberId.startsWith("SPLNO4AA4O");
    }
}


